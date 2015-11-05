/*
 * Copyright 2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gradle.model.dsl.internal.transform

import org.gradle.integtests.fixtures.AbstractIntegrationSpec

class NestedModelRuleDslDetectionIntegrationTest extends AbstractIntegrationSpec {
    def "rules can contain arbitrary code that includes closures that look like nested rules"() {
        buildFile << '''
class UnmanagedThing {
    def getSomeProp() {
        return this
    }
    def conf(Closure cl) {
        cl.delegate = this
        cl.call()
    }
}

class MyPlugin extends RuleSource {
    @Model
    UnmanagedThing thing() { return new UnmanagedThing() }
}
apply plugin: MyPlugin


model {
    thing {
        someProp.conf {
            // not a rule
            conf { }
        }
    }
    tasks {
        show(Task) { doLast { println $.thing } }
    }
}
'''

        expect:
        succeeds "show"
    }
}
