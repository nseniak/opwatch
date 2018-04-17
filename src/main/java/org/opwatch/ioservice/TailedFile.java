/*
 * Copyright (c) 2016-2017 by OMC Inc and other Opwatch contributors
 *
 * Licensed under the Apache License, Version 2.0  (the "License").  You may obtain
 * a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES
 * OR CONDITIONS OF ANY KIND, either express or implied.  See the License for
 * the specific language governing permissions and limitations under the License.
 */

package org.opwatch.ioservice;

import org.opwatch.service.Config;
import org.opwatch.service.ProcessorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

public class TailedFile {

    private static final Logger logger = LoggerFactory.getLogger(TailedFile.class);

    private Path file;
    private TailLineHandler handler;
    private Config config;

    public TailedFile(Config config, Path file, TailLineHandler handler) {
        this.config = config;
        this.file = file;
        this.handler = handler;
    }

    private static final String MESSAGE_PREFIX = "File tailer: ";

    public void tail(ProcessorService processorService) throws InterruptedException, IOException {
        TailedFileReader reader = null;
        boolean initialIteration = true;
        try {
            while (true) {
                reader = waitForFile(processorService);
                if (initialIteration) {
                    reader.skipToEnd();
                    initialIteration = false;
                }
                while (true) {
                    BasicFileAttributes attrs = fileAttributes(file);
                    if (attrs == null) {
                        if (config.tailedFileFileChangeAlerts()) {
                            processorService.signalSystemInfo(MESSAGE_PREFIX + "File deleted: " + file);
                        }
                        reader.safeClose();
                        break;
                    }
                    if (attrs.size() < reader.getAttributes().size()) {
                        // File has been truncated
                        if (config.tailedFileFileChangeAlerts()) {
                            processorService.signalSystemInfo(MESSAGE_PREFIX + "File truncated: " + file);
                        }
                        reader.safeClose();
                        break;
                    }
                    if (!attrs.fileKey().equals(reader.getAttributes().fileKey())) {
                        // File has changed
                        if (config.tailedFileFileChangeAlerts()) {
                            processorService.signalSystemInfo(MESSAGE_PREFIX + "File has changed: " + file);
                        }
                        reader.safeClose();
                    }
                    reader.updateAttributes();
                    String line = reader.readLine();
                    if (line == null) {
                        // Wait a bit
                        Thread.sleep(config.tailPollInterval());
                    } else {
                        reader.incrLineNumber();
                        handler.handle(line, reader.getLineNumber());
                    }
                }
            }
        } finally {
            if (reader != null) {
                reader.safeClose();
            }
        }
    }

    private class TailedFileReader {

        private Path file;
        private LineReader reader;
        private BasicFileAttributes attributes;
        private int lineNumber;

        public TailedFileReader(Path file, LineReader reader, BasicFileAttributes attributes) {
            this.reader = reader;
            this.attributes = attributes;
            this.lineNumber = 0;
        }

        String readLine() throws IOException, InterruptedException {
            return reader.readLine();
        }

        void updateAttributes() {
            attributes = fileAttributes(file);
        }

        void skipToEnd() throws IOException, InterruptedException {
            while (reader.readLine() != null) {
                lineNumber += 1;
            }
        }

        void safeClose() {
            try {
                reader.close();
            } catch (IOException e) {
                // Do nothing
            }
        }

        LineReader getReader() {
            return reader;
        }

        BasicFileAttributes getAttributes() {
            return attributes;
        }

        int getLineNumber() {
            return lineNumber;
        }

        void incrLineNumber() {
            lineNumber += 1;
        }
    }

    private TailedFileReader waitForFile(ProcessorService processorService) throws InterruptedException, IOException {
        boolean waitSignaled = false;
        BasicFileAttributes attributes;
        LineReader reader;
        long t0 = System.currentTimeMillis();
        while (((attributes = fileAttributes(file)) == null) || ((reader = openFile(file)) == null)) {
            long waitTime = System.currentTimeMillis() - t0;
            if (!waitSignaled && (waitTime > config.tailedFileMissingAlertDelay())) {
                processorService.signalSystemInfo(MESSAGE_PREFIX + "Waiting for file: " + file);
                waitSignaled = true;
            }
            Thread.sleep(config.tailedFileWatchingCheckDelay());
        }
        if (waitSignaled) {
            processorService.signalSystemInfo(MESSAGE_PREFIX + "File found: " + file);
        }
        return new TailedFileReader(file, reader, attributes);
    }

    private BasicFileAttributes fileAttributes(Path path) {
        try {
            return Files.readAttributes(file, BasicFileAttributes.class);
        } catch (IOException e) {
            return null;
        }
    }

    private LineReader openFile(Path path) {
        try {
            InputStream in = Files.newInputStream(path);
            return new LineReader(new BufferedInputStream(in), config.lineBufferSize(), false);
        } catch (IOException e) {
            return null;
        }
    }

    public Path getFile() {
        return file;
    }

    public void setFile(Path file) {
        this.file = file;
    }

    public TailLineHandler getHandler() {
        return handler;
    }

}
