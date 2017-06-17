import os
import re
from os.path import join

doc_root_dir = '../doc/'
index_file = doc_root_dir + 'examples.md'

md_file_regex = re.compile('.*\.md$')


def md_files(doc_root_dir):
    file_list = []
    for root, subdirs, files in os.walk(doc_root_dir):
        for file in [f for f in files if md_file_regex.match(f)]:
            file_list.append(join(root, file))
    return file_list


example_regex = re.compile('<!-- example-begin -->\n#+([^\n]*)(\n.*?)<!-- example-end -->', re.DOTALL)
ignored_char_regex = re.compile('[^0-9a-zA-Z ]')


def file_examples(file):
    with open(file, 'r') as file_desc:
        content = file_desc.read()
    examples = []
    for match in re.finditer(example_regex, content):
        title = match.group(1).strip()
        body = match.group(2)
        stripped_title = ignored_char_regex.sub('', title.lower())
        anchor = re.sub(' ', '-', stripped_title)
        examples.append({'title': title, 'body': body, 'anchor': anchor})
    return examples


index_begin = '<!-- example-list-begin -->'
index_end = '<!-- example-list-end -->'
index_regex = re.compile(re.escape(index_begin) + '\n.*' + re.escape(index_end), re.DOTALL)

items = []
for file in md_files(doc_root_dir):
    for example in file_examples(file):
        title = example['title']
        body = example['body']
        anchor = example['anchor']
        items.append('#### [' + title + '](' + file[len(doc_root_dir):] + "#" + anchor + ')' + body)
list = '\n'.join(items)
with open(index_file, 'r') as index_file_desc:
    content = index_file_desc.read()
new_content = index_regex.sub(index_begin + '\n' + list + '\n' + index_end, content)

with open(index_file, 'w') as index_file_desc:
    index_file_desc.write(new_content)

