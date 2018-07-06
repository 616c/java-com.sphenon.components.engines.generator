package com.sphenon.engines.generator.test;

/****************************************************************************
  Copyright 2001-2018 Sphenon GmbH

  Licensed under the Apache License, Version 2.0 (the "License"); you may not
  use this file except in compliance with the License. You may obtain a copy
  of the License at http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
  WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
  License for the specific language governing permissions and limitations
  under the License.
*****************************************************************************/

import com.sphenon.basics.context.*;
import com.sphenon.basics.monitoring.*;
import com.sphenon.basics.notification.*;
import com.sphenon.basics.customary.*;
import com.sphenon.basics.expression.*;
import com.sphenon.basics.testing.*;

public class Test_Basics extends com.sphenon.basics.testing.classes.TestBase {

    public Test_Basics (CallContext context) {
    }

    public String getId(CallContext context) {
        if (this.id == null) {
            this.id = "GeneratorBasics";
        }
        return this.id;
    }

    public TestResult perform (CallContext context, TestRun test_run) {

        try {

            String s1 = "g:G-2.0-plain_java_jspstyle-1.0\nHallo 3 * 7 = <%= 3 * 7 %>";
            CustomaryContext.create((Context)context).sendTrace(context, Notifier.CHECKPOINT, "Dynamic string '%(string)' => '%(result)'", "string", s1, "result", (new DynamicString(context, s1)).get(context));

        } catch (Throwable t) {
            return new TestResult_ExceptionRaised(context, t);
        }
        
        return TestResult.OK;
    }
}

