/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import common_job_properties

// Defines a job.
job('beam_PreCommit_Website_Merge') {
  description('Runs website tests for mergebot.')

  // Set common parameters.
  common_job_properties.setTopLevelWebsiteJobProperties(delegate, 'mergebot')

  triggers {
    githubPush()
  }

  steps {
    // Run the following shell script as a build step.
    shell '''
        # Install RVM per instructions at https://rvm.io/rvm/install.
        gpg --keyserver hkp://keys.gnupg.net --recv-keys \\
            409B6B1796C275462A1703113804BB82D39DC0E3 \\
            7D2BAF1CF37B13E2069D6956105BD0E739499BDB

        \\curl -sSL https://get.rvm.io | bash
        source /home/jenkins/.rvm/scripts/rvm

        # Install Ruby.
        RUBY_VERSION_NUM=2.3.0
        rvm install ruby $RUBY_VERSION_NUM --autolibs=read-only

        # Install Bundler gem
        PATH=~/.gem/ruby/$RUBY_VERSION_NUM/bin:$PATH
        GEM_PATH=~/.gem/ruby/$RUBY_VERSION_NUM/:$GEM_PATH
        gem install bundler --user-install

        # Enter the git clone for remaining commands
        cd src

        # Install all needed gems.
        bundle install --path ~/.gem/

        # Build the new site and test it.
        rm -fr ./content/
        bundle exec rake test
    '''.stripIndent().trim()
  }
}
