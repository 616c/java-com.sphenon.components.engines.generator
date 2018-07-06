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
import com.sphenon.basics.testing.TestRun;
import com.sphenon.basics.testing.TestResult;
import com.sphenon.basics.testing.TestResult_ExceptionRaised;

import com.sphenon.engines.factorysite.*;
import com.sphenon.engines.factorysite.factories.*;
import com.sphenon.engines.factorysite.gates.*;

import com.sphenon.basics.many.tplinst.*;
import com.sphenon.engines.generator.*;

public class Test_GeneratorClassLoader extends com.sphenon.basics.testing.classes.TestBase {

    public Test_GeneratorClassLoader (CallContext context) {
    }

    public String getId(CallContext context) {
        if (this.id == null) {
            this.id = "GeneratorClassLoader:" + this.generator_class;
        }
        return this.id;
    }

    protected String generator_class;

    public String getGeneratorClass (CallContext context) {
        return this.generator_class;
    }

    public void setGeneratorClass (CallContext context, String generator_class) {
        this.generator_class = generator_class;
    }

    public TestResult perform (CallContext call_context, TestRun test_run) {
        Context context = Context.create(call_context);

        try {
            Class gc = Class.forName(this.generator_class, true, new GeneratorClassLoader(context));
        } catch (Throwable t) {
            return new TestResult_ExceptionRaised(context, t);
        }
        
        return TestResult.OK;
    }
}
