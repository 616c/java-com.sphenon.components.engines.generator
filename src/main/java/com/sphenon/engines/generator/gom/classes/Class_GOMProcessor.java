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

import java.util.Stack;

public class Class_GOMProcessor implements GOMProcessor {
    static protected long notification_level;
    static public    long adjustNotificationLevel(long new_level) { long old_level = notification_level; notification_level = new_level; return old_level; }
    static public    long getNotificationLevel() { return notification_level; }
    static { notification_level = NotificationLocationContext.getLevel(RootContext.getInitialisationContext(), "com.sphenon.engines.generator.gom.classes.Class_GOMProcessor"); };

    protected Stack<GOMCallStackEntry> call_stack;

    public Class_GOMProcessor (CallContext context, GOMNode gom_node, GOMExecutionContext gom_execution_context, GeneratorOutputHandler output_handler, Object... arguments) {
        this.call_stack = new Stack<GOMCallStackEntry>();
        this.call_stack.push(new GOMCallStackEntry (gom_node, gom_execution_context, output_handler, arguments));
    }

    public void process (CallContext context) {
        if ((this.notification_level & Notifier.SELF_DIAGNOSTICS) != 0) { CustomaryContext.create((Context)context).sendTrace(context, Notifier.SELF_DIAGNOSTICS, "processing..."); }
        while (this.call_stack.empty() == false) {
            GOMCallStackEntry cse = this.call_stack.peek();
            if ((this.notification_level & Notifier.SELF_DIAGNOSTICS) != 0) { CustomaryContext.create((Context)context).sendTrace(context, Notifier.SELF_DIAGNOSTICS, "processing node '%(nodeid)'/'%(nodetype)'...", "nodeid", cse.gom_node.getId(context), "nodetype", cse.gom_node.getClass().getName()); }
            if (cse.gom_node.generate(context, this, cse.gom_execution_context, cse.output_handler, cse.arguments) == false) {
                break;
            }
        }
        if ((this.notification_level & Notifier.SELF_DIAGNOSTICS) != 0) { CustomaryContext.create((Context)context).sendTrace(context, Notifier.SELF_DIAGNOSTICS, "processing done"); }
    }

    public void process (CallContext context, int local_gom_index, int gom_id) {
        if ((this.notification_level & Notifier.SELF_DIAGNOSTICS) != 0) { CustomaryContext.create((Context)context).sendTrace(context, Notifier.SELF_DIAGNOSTICS, "processing..."); }
        boolean first = true;
        while (this.call_stack.empty() == false) {
            GOMCallStackEntry cse = this.call_stack.peek();
            if (first) {
                first = false;
                if (local_gom_index != -1) {
                    if ((this.notification_level & Notifier.SELF_DIAGNOSTICS) != 0) { CustomaryContext.create((Context)context).sendTrace(context, Notifier.SELF_DIAGNOSTICS, "seek to child #'%(index)' in node '%(nodeid)'/'%(nodetype)', expected child id '%(childid)'...", "index", local_gom_index, "nodeid", cse.gom_node.getId(context), "nodetype", cse.gom_node.getClass().getName(), "childid", gom_id); }
                    cse.gom_node.seek(context, this, local_gom_index);
                    assert cse.gom_node.getChildNodes(context).elementAt(local_gom_index).getId(context) == gom_id : ("Element id " + cse.gom_node.getChildNodes(context).elementAt(local_gom_index).getId(context) + " == " + gom_id);
                } else {
                    assert cse.gom_node.getId(context) == gom_id : ("Element id " + cse.gom_node.getId(context) + " == " + gom_id);
                }
            }
            if ((this.notification_level & Notifier.SELF_DIAGNOSTICS) != 0) { CustomaryContext.create((Context)context).sendTrace(context, Notifier.SELF_DIAGNOSTICS, "processing node '%(nodeid)'/'%(nodetype)'...", "nodeid", cse.gom_node.getId(context), "nodetype", cse.gom_node.getClass().getName()); }
            if (cse.gom_node.generate(context, this, cse.gom_execution_context, cse.output_handler, cse.arguments) == false) {
                break;
            }
        }
        if ((this.notification_level & Notifier.SELF_DIAGNOSTICS) != 0) { CustomaryContext.create((Context)context).sendTrace(context, Notifier.SELF_DIAGNOSTICS, "processing done"); }
    }

    public Stack<GOMCallStackEntry> getCallStack (CallContext context) {
        return this.call_stack;
    }

    public void push (CallContext context, GOMNode gom_node, GOMExecutionContext gom_execution_context, GeneratorOutputHandler output_handler, Object... arguments) {
        assert gom_node != null : ("gom_node != null");
        this.call_stack.push(new GOMCallStackEntry (gom_node, gom_execution_context, output_handler, arguments));
        if ((this.notification_level & Notifier.SELF_DIAGNOSTICS) != 0) { CustomaryContext.create((Context)context).sendTrace(context, Notifier.SELF_DIAGNOSTICS, "push '%(node)'...", "node", gom_node.getClass().getName().replaceFirst(".*\\.","")); }
    }

    public void pop (CallContext context) {
        this.call_stack.pop();
        if (this.call_stack.empty()) {
            if ((this.notification_level & Notifier.SELF_DIAGNOSTICS) != 0) { CustomaryContext.create((Context)context).sendTrace(context, Notifier.SELF_DIAGNOSTICS, "pop, now empty..."); }
        } else {
            if ((this.notification_level & Notifier.SELF_DIAGNOSTICS) != 0) { CustomaryContext.create((Context)context).sendTrace(context, Notifier.SELF_DIAGNOSTICS, "pop, now '%(node)', local.index '%(localindex)'...", "node", this.call_stack.peek().gom_node.getClass().getName().replaceFirst(".*\\.",""), "localindex", ((Class_GOMLocalVariables)(this.call_stack.peek().local_variables)).index); }
        }
    }

    public GOMLocalVariables getLocalVariables (CallContext context) {
        return this.call_stack.empty() ? null : this.call_stack.peek().local_variables;
    }

    public void setLocalVariables (CallContext context, GOMLocalVariables local_variables) {
        if (this.call_stack.empty() == false) {
            this.call_stack.peek().local_variables = local_variables;
        }
    }

    public GOMNode getCurrentNode (CallContext context) {
        return this.call_stack.empty() ? null : this.call_stack.peek().gom_node;
    }
}
