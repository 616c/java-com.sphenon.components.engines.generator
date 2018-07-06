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
import com.sphenon.engines.generator.classes.*;

public class Test_Generator_InMemory extends com.sphenon.basics.testing.classes.TestBase {

    public Test_Generator_InMemory (CallContext context) {
    }

    public String getId(CallContext context) {
        if (this.id == null) {
            this.id = "Generator_InMemory";
        }
        return this.id;
    }

    protected String template_code;

    public String getTemplateCode (CallContext context) {
        return this.template_code;
    }

    public void setTemplateCode (CallContext context, String template_code) {
        this.template_code = template_code;
    }

    protected GeneratorOutputHandler generator_output_handler;

    public GeneratorOutputHandler getGeneratorOutputHandler (CallContext context) {
        return this.generator_output_handler;
    }

    public void setGeneratorOutputHandler (CallContext context, GeneratorOutputHandler generator_output_handler) {
        this.generator_output_handler = generator_output_handler;
    }

    protected Vector_Object_long_ arguments;

    public Vector_Object_long_ getArguments (CallContext context) {
        return this.arguments;
    }

    public void setArguments (CallContext context, Vector_Object_long_ arguments) {
        this.arguments = arguments;
    }

    public TestResult perform (CallContext call_context, TestRun test_run) {
        Context context = Context.create(call_context);

        try {
            Generator generator = GeneratorRegistry.get(context).getGenerator_InMemoryTemplate(context, template_code);

            Object[] o_args = new Object[(int)arguments.getSize(context)];
            for (int i=0; i<arguments.getSize(context); i++) {
                o_args[i] = arguments.tryGet(context, i);
            }
            generator.generate(context, generator_output_handler, o_args);

            if (generator_output_handler instanceof GeneratorOutputToString) {
                ((GeneratorOutputToString)generator_output_handler).getResult(context);
            }
        } catch (Throwable t) {
            return new TestResult_ExceptionRaised(context, t);
        }
        
        return TestResult.OK;
    }
}
