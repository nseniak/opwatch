import os
import re
from os.path import join

# Root of the documentation directory
doc_root_dir = '../doc/'
# Target file for the list of examples
list_file = doc_root_dir + 'examples.md'

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


index_begin = '<!-- example-index-begin -->'
index_end = '<!-- example-index-end -->'
index_regex = re.compile(re.escape(index_begin) + '\n.*' + re.escape(index_end), re.DOTALL)
list_begin = '<!-- example-list-begin -->'
list_end = '<!-- example-list-end -->'
list_regex = re.compile(re.escape(list_begin) + '\n.*' + re.escape(list_end), re.DOTALL)

print('Generating the example list in', list_file)

index = []
examples = []
for file in md_files(doc_root_dir):
    for example in file_examples(file):
        title = example['title']
        body = example['body']
        anchor = example['anchor']
        examples.append('---\n#### [' + title + '](' + file[len(doc_root_dir):] + "#" + anchor + ')' + body)
        index.append('- [' + title + '](#' + anchor + ')')
index_list = '\n'.join(index)
example_list = '\n'.join(examples)
with open(list_file, 'r') as list_file_desc:
    content = list_file_desc.read()
new_content = list_regex.sub(list_begin + '\n' + example_list + '\n' + list_end, content)
new_content = index_regex.sub(index_begin + '\n' + index_list + '\n' + index_end, new_content)

if (new_content == content):
    print('The content of', list_file, ' has not changed.')
else:
    with open(list_file, 'w') as list_file_desc:
        list_file_desc.write(new_content)
    print('The content of', list_file, ' has changed. Please commit it.')
