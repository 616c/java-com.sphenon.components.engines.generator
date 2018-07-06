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

import java.util.Vector;

abstract public class Class_GOMNode implements GOMNode {
    static protected long notification_level;
    static public    long adjustNotificationLevel(long new_level) { long old_level = notification_level; notification_level = new_level; return old_level; }
    static public    long getNotificationLevel() { return notification_level; }
    static { notification_level = NotificationLocationContext.getLevel(RootContext.getInitialisationContext(), "com.sphenon.engines.generator.Class_GOMNode"); };

    public Class_GOMNode(CallContext context, GOMNode parent_node, int id) {
        this.id          = id;
        this.parent_node = parent_node;
        if (this.parent_node == null) {
            this.root_node = this;
        } else {
            this.parent_node.getChildNodes(context).add(this);
            this.root_node = this.parent_node.getRootNode(context);
        }
    }

    protected int             id;
    protected GOMNode         parent_node;
    protected GOMNode         root_node;
    protected Vector<GOMNode> child_nodes;

    public int getId (CallContext context) {
        return this.id;
    }

    public GOMNode getRootNode (CallContext context) {
        return this.root_node;
    }

    public GOMNode getParentNode (CallContext context) {
        return this.parent_node;
    }

    public Vector<GOMNode> getChildNodes (CallContext context) {
        if (this.child_nodes == null) {
            this.child_nodes = new Vector<GOMNode>();
        }
        return this.child_nodes;
    }

    public void seek (CallContext context, GOMProcessor gom_processor, int child_index) {
        Class_GOMLocalVariables local = (Class_GOMLocalVariables) gom_processor.getLocalVariables(context);
        assert local != null : ("Local in seek is null (" + this.getClass().getName() + ")");
        local.index = child_index;
    }

    public void dumpGOMTree(CallContext context) {
        if ((this.notification_level & Notifier.SELF_DIAGNOSTICS) != 0) {
            CustomaryContext.create((Context)context).sendTrace(context, Notifier.SELF_DIAGNOSTICS, "--------- GOM Tree Begin ---------");
            dumpGOMTree(context, "");
            CustomaryContext.create((Context)context).sendTrace(context, Notifier.SELF_DIAGNOSTICS, "--------- GOM Tree End -----------");
        }
    }

    public void dumpGOMTree(CallContext context, String indent) {
        if ((this.notification_level & Notifier.SELF_DIAGNOSTICS) != 0) {
            CustomaryContext.create((Context)context).sendTrace(context, Notifier.SELF_DIAGNOSTICS, indent + "'%(id)' : '%(class)'", "id", this.id, "class", this.getClass().getName().replaceFirst(".*\\.",""));
            this.dumpGOMNodeDetails(context, indent);
            int i=0;
            for (GOMNode child : this.getChildNodes(context)) {
                CustomaryContext.create((Context)context).sendTrace(context, Notifier.SELF_DIAGNOSTICS, indent + "  child %(index)", "index", i);
                child.dumpGOMTree(context, indent + "    ");
                i++;
            }
        }
    }

    public void dumpGOMNodeDetails(CallContext context, String indent) {
    }
}
