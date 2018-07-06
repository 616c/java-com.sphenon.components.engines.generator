package com.sphenon.engines.generator.classes;

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
import com.sphenon.basics.debug.*;
import com.sphenon.basics.tracking.*;
import com.sphenon.basics.exception.*;
import com.sphenon.basics.notification.*;
import com.sphenon.basics.customary.*;
import com.sphenon.basics.data.*;
import com.sphenon.basics.javacode.*;

import com.sphenon.engines.generator.*;
import com.sphenon.engines.generator.tom.*;
import com.sphenon.engines.generator.tom.classes.*;
import com.sphenon.engines.generator.returncodes.*;

import java.io.BufferedReader;
import java.io.Writer;
import java.io.IOException;

public class Class_TemplateParser implements TemplateParser, OriginSlot {

    static final public Class _class = Class_TemplateParser.class;

    // Configuration ------------------------------------------------------------------
    static protected long notification_level;
    static public    long adjustNotificationLevel(long new_level) { long old_level = notification_level; notification_level = new_level; return old_level; }
    static public    long getNotificationLevel() { return notification_level; }
    static { notification_level = NotificationLocationContext.getLevel(_class); };

    static protected long runtimestep_level;
    static public    long adjustRuntimeStepLevel(long new_level) { long old_level = runtimestep_level; runtimestep_level = new_level; return old_level; }
    static public    long getRuntimeStepLevel() { return runtimestep_level; }
    static { runtimestep_level = RuntimeStepLocationContext.getLevel(_class); };

    // Construction -------------------------------------------------------------------

    public Class_TemplateParser(CallContext context) {
    }

    // Attributes ---------------------------------------------------------------------

    protected TOMParser tom_parser;

    public TOMParser getTOMParser (CallContext context) {
        return this.tom_parser;
    }

    public void setTOMParser (CallContext context, TOMParser tom_parser) {
        this.tom_parser = tom_parser;
    }

    public long getLastModification(CallContext context) {
        return this.origin.getLastModification(context);
    }

    public String getId(CallContext context) {
        return "";
    }

    protected Origin origin;

    public Origin getOrigin (CallContext context) {
        return this.origin;
    }

    public void setOrigin (CallContext context, Origin origin) {
        this.origin = origin;
    }

    public Origin defaultOrigin (CallContext context) {
        return null;
    }

    // Internal -----------------------------------------------------------------------

    // Operations ---------------------------------------------------------------------

    // --------------------------------------------------------------------------------

    public TOMNode parse (CallContext call_context, Template template, JavaCodeManager java_code_manager, TOMNode current_node) throws InvalidTemplateSyntax {
        Context context = Context.create(call_context);
        RuntimeStep runtime_step = null;

        BufferedReader reader = template.getReader(context);

        try {
            if ((runtimestep_level & RuntimeStepLevel.OBSERVATION_CHECKPOINT) != 0) { runtime_step = RuntimeStep.create(context, RuntimeStepLevel.OBSERVATION_CHECKPOINT, _class, "Parsing template '%(template)'", "template", template.getFullClassName(context)); }

            current_node = tom_parser.parse(context, template, current_node);

        } catch(ExceptionError t) {
            if (runtime_step != null) { runtime_step.setFailed(context, t, ""); runtime_step = null; }
            throw t;
        }
        if (runtime_step != null) { runtime_step.setCompleted(context, ""); runtime_step = null; }

        if ((notification_level & Notifier.SELF_DIAGNOSTICS) != 0) { NotificationContext.dump(context, "TOMTree", current_node, Notifier.SELF_DIAGNOSTICS); }

        if (java_code_manager != null) {
            current_node.createJavaCode(context, TOMNode.Section.NO_SECTION, java_code_manager, null, null);
        }

        return current_node;
    }
}
