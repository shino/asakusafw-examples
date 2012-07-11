#!/bin/sh -e
#
# Copyright 2011-2012 Asakusa Framework Team.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
PROJECT_NAME=example-seqfile

if [ "$ASAKUSA_HOME" = "" ]
then
    echo '$ASAKUSA_HOME'" is not defined" 1>&2
    exit 1
fi

BASENAME=$(basename `pwd`)
if [ ! "$BASENAME" = "$PROJECT_NAME" ]
then
    echo "This script need to run on project-root directory." 1>&2
    exit 1
fi

rm "$ASAKUSA_HOME"/batchapps/* -fr
cp -pr target/batchc/* "$ASAKUSA_HOME"/batchapps

PROJECT_PATH=$PWD
cd $HOME
set +e
rm target/testing/directio -fr
hadoop fs -rmr target/testing/directio
set -e
hadoop fs -put "$PROJECT_PATH"/src/test/example-dataset/master target/testing/directio/master
hadoop fs -put "$PROJECT_PATH"/src/test/example-dataset/sales target/testing/directio/sales
