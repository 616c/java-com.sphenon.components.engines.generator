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
import com.sphenon.basics.context.classes.*;
import com.sphenon.basics.notification.*;
import com.sphenon.basics.monitoring.*;
import com.sphenon.basics.testing.TestRun;
import com.sphenon.basics.testing.TestResult;
import com.sphenon.basics.testing.TestResult_ExceptionRaised;
import com.sphenon.basics.doclet.*;

import com.sphenon.engines.factorysite.*;
import com.sphenon.engines.factorysite.factories.*;
import com.sphenon.engines.factorysite.gates.*;

import com.sphenon.basics.many.tplinst.*;
import com.sphenon.engines.generator.*;
import com.sphenon.engines.generator.classes.*;

import java.util.Vector;

public class Test_Generator extends com.sphenon.basics.testing.classes.TestBase {
    static protected long notification_level;
    static public    long adjustNotificationLevel(long new_level) { long old_level = notification_level; notification_level = new_level; return old_level; }
    static public    long getNotificationLevel() { return notification_level; }
    static { notification_level = NotificationLocationContext.getLevel(RootContext.getInitialisationContext(), "com.sphenon.engines.generator.test.Test_Generator"); };

    public Test_Generator (CallContext context) {
    }

    public String getId(CallContext context) {
        if (this.id == null) {
            this.id = "Generator:" + this.generator_class;
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
            Generator generator = GeneratorRegistry.get(context).getGenerator(context, generator_class);

            Object[] o_args = new Object[(int)arguments.getSize(context)];
            for (int i=0; i<arguments.getSize(context); i++) {
                o_args[i] = arguments.tryGet(context, i);
            }

            if ((notification_level & Notifier.CHECKPOINT) != 0) {
                Vector<FormalArgument>   signature     = generator.getSignature(context);
                String                   instance_name = generator.getInstanceName(context, o_args);
                MetaDataRequirements     mdr           = generator.getMetaData(context, MetaDataRequirements.class, true, o_args);
                Vector<TemplateInstance> requirements  = mdr == null ? null : mdr.getRequirements(context);
                MetaDataDoclets          mdd           = generator.getMetaData(context, MetaDataDoclets.class, true, o_args);
                Vector<Doclet>           doclets       = mdd == null ? null : mdd.getDoclets(context);

                if (signature != null) {
                    for (FormalArgument fa : signature) {
                        NotificationContext.sendCheckpoint(context, "FormalArgument: '%(name)' - '%(type)'", "name", fa.getArgumentName(context), "type", fa.getTypeName(context));
                    }
                }
                
                if (instance_name != null) {
                    NotificationContext.sendCheckpoint(context, "InstanceName: '%(name)'", "name", instance_name);
                }
                
                if (requirements != null) {
                    for (TemplateInstance ti : requirements) {
                        NotificationContext.sendCheckpoint(context, "Requirement: '%(instance)'", "instance", ti);
                    }
                }

                if (doclets != null) {
                    for (Doclet doclet : doclets) {
                        NotificationContext.sendCheckpoint(context, "Doclet: '%(doclet)'", "doclet", doclet.getDocBook(context));
                    }
                }
            }
            
            generator.generate(context, generator_output_handler, o_args);

            if (generator_output_handler instanceof GeneratorOutputToString) {
                GeneratorOutputToString gots = (GeneratorOutputToString) generator_output_handler;
                Vector<String> cns = gots.getChannelNames(context);
                if (cns == null) {
                    gots.getResult(context);
                } else {
                    for (String cn : cns) {
                        gots.getResult(context, cn);
                    }
                }
            }
        } catch (Throwable t) {
            return new TestResult_ExceptionRaised(context, t);
        }
        
        return TestResult.OK;
    }
}
