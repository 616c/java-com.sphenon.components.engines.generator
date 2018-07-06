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
import com.sphenon.basics.encoding.*;
import com.sphenon.basics.data.*;

import com.sphenon.engines.generator.*;
import com.sphenon.engines.generator.classes.*;
import com.sphenon.engines.generator.gom.*;

public class GOMText extends Class_GOMNode {
    static protected long notification_level;
    static public    long adjustNotificationLevel(long new_level) { long old_level = notification_level; notification_level = new_level; return old_level; }
    static public    long getNotificationLevel() { return notification_level; }
    static { notification_level = NotificationLocationContext.getLevel(RootContext.getInitialisationContext(), "com.sphenon.engines.generator.GOMText"); };

    // Configuration ------------------------------------------------------------------

    // Construction -------------------------------------------------------------------

    public GOMText(CallContext context, GOMNode parent, int id) {
        super(context, parent, id);
    }

    public GOMText(CallContext context, GOMNode parent, int id, String text) {
        super(context, parent, id);
        this.text = text;
    }

    // Attributes ---------------------------------------------------------------------

    protected String text;

    public String getText (CallContext context) {
        return this.text;
    }

    public void setText (CallContext context, String text) {
        this.text = text;
    }

    // Internal -----------------------------------------------------------------------

    // Operations ---------------------------------------------------------------------

    // --------------------------------------------------------------------------------

    public boolean generate (CallContext context, GOMProcessor gom_processor, GOMExecutionContext gom_execution_context, GeneratorOutputHandler output_handler, Object... arguments) {
        // EncodingStep[] steps = ((Class_GOMExecutionContext) gom_execution_context).getTextEncodingSteps(context);
        // ((Class_GOMExecutionContext) gom_execution_context).getOutput(context, true).write(steps == null ? this.text : Encoding.recode(context, this.text, steps));

        ((Class_GOMExecutionContext) gom_execution_context).getOutput(context, true).write(this.text);

        gom_processor.pop(context);
        return true;
    }

    public void dumpGOMNodeDetails(CallContext context, String indent) {
        if ((this.notification_level & Notifier.SELF_DIAGNOSTICS) != 0) { CustomaryContext.create((Context)context).sendTrace(context, Notifier.SELF_DIAGNOSTICS, indent + "  text '%(text)'", "text", (this.text.length() > 32 ? this.text.substring(0,32) : this.text).replaceAll("\n","\\\\n")); }
    }
}
