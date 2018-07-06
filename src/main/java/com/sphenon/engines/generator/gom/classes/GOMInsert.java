package com.sphenon.engines.generator.gom.classes;

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
import com.sphenon.basics.exception.*;
import com.sphenon.basics.notification.*;
import com.sphenon.basics.customary.*;
import com.sphenon.basics.data.*;

import com.sphenon.engines.generator.*;
import com.sphenon.engines.generator.classes.*;
import com.sphenon.engines.generator.gom.*;

import java.io.Writer;
import java.io.PrintWriter;

abstract public class GOMInsert extends Class_GOMNode {
    static protected long notification_level;
    static public    long adjustNotificationLevel(long new_level) { long old_level = notification_level; notification_level = new_level; return old_level; }
    static public    long getNotificationLevel() { return notification_level; }
    static { notification_level = NotificationLocationContext.getLevel(RootContext.getInitialisationContext(), "com.sphenon.engines.generator.GOMInsert"); };

    // Configuration ------------------------------------------------------------------

    // Construction -------------------------------------------------------------------

    // public GOMInsert(CallContext context, int id) {
    //     super(context, null, id);
    // }

    public GOMInsert(CallContext context, GOMNode parent, int id) {
        super(context, parent, id);
    }

    // Attributes ---------------------------------------------------------------------

    // this points to the associated subtemplate gom node of the insert statement
    abstract public GOMNode getTemplate(CallContext context, GOMExecutionContext gom_execution_context);

    abstract public String getTemplateId(CallContext context);

    // Internal -----------------------------------------------------------------------

    protected class MyLocalVariables extends Class_GOMLocalVariables {
    }

    // Operations ---------------------------------------------------------------------

    // --------------------------------------------------------------------------------

    public boolean generate (CallContext context, GOMProcessor gom_processor, GOMExecutionContext gom_execution_context, GeneratorOutputHandler output_handler, Object... arguments) {
        MyLocalVariables local = (MyLocalVariables) gom_processor.getLocalVariables(context);
        if (local == null) {
            local = new MyLocalVariables();
            gom_processor.setLocalVariables(context, local);
        } else {
            local.index++;
        }

        if (local.index == 0) {
            gom_processor.push(context, this.getTemplate(context, gom_execution_context), gom_execution_context, output_handler, arguments);
        } else {
            gom_processor.setLocalVariables(context, null);
            gom_processor.pop(context);
        }
        return false;
    }

    public void dumpGOMNodeDetails(CallContext context, String indent) {
        if ((this.notification_level & Notifier.SELF_DIAGNOSTICS) != 0) { CustomaryContext.create((Context)context).sendTrace(context, Notifier.SELF_DIAGNOSTICS, indent + "  template '%(template)'", "template", this.getTemplateId(context)); }
    }
}
