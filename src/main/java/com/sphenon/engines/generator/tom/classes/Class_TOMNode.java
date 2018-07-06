package com.sphenon.engines.generator.tom.classes;

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
import com.sphenon.basics.exception.*;
import com.sphenon.basics.notification.*;
import com.sphenon.basics.customary.*;
import com.sphenon.basics.data.*;

import com.sphenon.engines.generator.*;
import com.sphenon.engines.generator.classes.*;
import com.sphenon.engines.generator.tom.*;

import java.util.Vector;

abstract public class Class_TOMNode implements TOMNode, Dumpable {

    public Class_TOMNode(CallContext context, TOMNode parent_node) {
        // System.err.println("new TOM: " + this + " (parent: " + parent_node + ")");
        this.parent_node = parent_node;
        if (this.parent_node == null) {
            this.root_node = (TOMRoot) this;
        } else {
            this.parent_node.getChildNodes(context).add(this);
            this.root_node = (TOMRoot) (this.parent_node.getRootNode(context));
        }
    }

    protected TOMNode parent_node;
    protected TOMRoot root_node;
    protected Vector<TOMNode> child_nodes;

    public TOMRoot getRootNode (CallContext context) {
        return this.root_node;
    }

    public TOMNode getParentNode (CallContext context) {
        return this.parent_node;
    }

    static protected class TOMConditionTrue implements TOMCondition {
        public boolean test(CallContext context, TOMNode tom_node) {
            return true;
        }
    }

    public<NodeClass extends TOMNode> NodeClass findSubNode(CallContext context, Class<NodeClass> node_class) {
        return findSubNode(context, node_class, new TOMConditionTrue());
    }

    public<NodeClass extends TOMNode> NodeClass findSubNode(CallContext context, Class<NodeClass> node_class, TOMCondition tom_condition) {
        return findSubNode(context, node_class, tom_condition, false);
    }

    public<NodeClass extends TOMNode> NodeClass findSubNode(CallContext context, Class<NodeClass> node_class, TOMCondition tom_condition, boolean mandatory) {
        for (TOMNode child : this.getChildNodes(context)) {
            if (    node_class.isAssignableFrom(child.getClass())
                 && tom_condition.test(context, child)) {
                return (NodeClass) child;
            }
            NodeClass result = child.findSubNode(context, node_class, tom_condition);
            if (result != null) { return result; }
        }
        if (mandatory) {
            CustomaryContext.create((Context)context).throwConfigurationError(context, "Cannot find '%(class)' sub node in TOM tree", "class", node_class.getName());
            throw (ExceptionConfigurationError) null; // compiler insists
        }
        return null;
    }

    public<NodeClass extends TOMNode> NodeClass findSuperNode(CallContext context, Class<NodeClass> node_class) {
        return findSuperNode(context, node_class, new TOMConditionTrue());
    }

    public<NodeClass extends TOMNode> NodeClass findSuperNode(CallContext context, Class<NodeClass> node_class, TOMCondition tom_condition) {
        return findSuperNode(context, node_class, tom_condition, false);
    }

    public<NodeClass extends TOMNode> NodeClass findSuperNode(CallContext context, Class<NodeClass> node_class, TOMCondition tom_condition, boolean mandatory) {
        TOMNode parent = this.getParentNode(context);
        if (parent != null) {
            if (    node_class.isAssignableFrom(parent.getClass())
                 && (tom_condition == null || tom_condition.test(context, parent))) {
                return (NodeClass) parent;
            } else {
                return parent.findSuperNode (context, node_class, tom_condition);
            }
        }
        if (mandatory) {
            CustomaryContext.create((Context)context).throwConfigurationError(context, "Cannot find '%(class)' super node in TOM tree", "class", node_class.getName());
            throw (ExceptionConfigurationError) null; // compiler insists
        }
        return null;
    }

    public<NodeClass extends TOMNode> NodeClass findPreceedingNode (CallContext context, Class<NodeClass> node_class) {
        return findPreceedingNode (context, node_class, new TOMConditionTrue());
    }

    public<NodeClass extends TOMNode> NodeClass findPreceedingNode (CallContext context, Class<NodeClass> node_class, TOMCondition tom_condition) {
        return findPreceedingNode (context, node_class, tom_condition, false);
    }

    public<NodeClass extends TOMNode> NodeClass findPreceedingNode (CallContext context, Class<NodeClass> node_class, TOMCondition tom_condition, boolean mandatory) {
        return findPreceedingNode (context, node_class, tom_condition, mandatory, false);
    }

    public<NodeClass extends TOMNode> NodeClass findPreceedingNode (CallContext context, Class<NodeClass> node_class, TOMCondition tom_condition, boolean mandatory, boolean include_start_node) {
        if (this.parent_node != null) {
            NodeClass found_node = null;
            Vector<TOMNode> childs = this.parent_node.getChildNodes(context);
            int index = 0;
            for (TOMNode c1 : childs) {
                if (c1 == this) {
                    if ( ! include_start_node) {
                        index -= 1;
                    }
                    for (; index >= 0; index--) {
                        TOMNode c2 = childs.get(index);
                        if (    node_class.isAssignableFrom(c2.getClass())
                             && (tom_condition == null || tom_condition.test(context, c2))
                           ) {
                            found_node = (NodeClass) c2;
                            break;
                        }
                    }
                    break;
                }
                index++;
            }
            if (found_node != null) { 
return found_node; }
            return this.parent_node.findPreceedingNode (context, node_class, tom_condition, mandatory);
        }
        if (mandatory) {
            CustomaryContext.create((Context)context).throwConfigurationError(context, "Cannot find '%(class)' preceeding node in TOM tree", "class", node_class.getName());
            throw (ExceptionConfigurationError) null; // compiler insists
        }
        return null;
    }

    public Vector<TOMNode> getChildNodes (CallContext context) {
        if (this.child_nodes == null) {
            this.child_nodes = new Vector<TOMNode>();
        }
        return this.child_nodes;
    }

    protected TOMProperties properties;

    public TOMProperties getProperties (CallContext context) {
        if (this.properties == null) {
            this.properties = new Class_TOMProperties(context);
        }
        return this.properties;
    }

    public void setProperties (CallContext context, TOMProperties properties) {
        this.properties = properties;
    }

    public Recoding getRecoding(CallContext context) {
        return (this.properties == null ? null : this.properties.getRecoding(context));
    }

    public Recoding getTextRecoding(CallContext context) {
        return (this.properties == null ? null : this.properties.getTextRecoding(context));
    }

    public void dump(CallContext context, DumpNode dump_node) {
        dump_node.dump(context, "TOMNode", this.getClass().getName().replaceFirst(".*\\.",""));
        Vector<TOMNode> childs = this.getChildNodes(context);
        if (childs != null && childs.size() != 0) {
            DumpNode dn = dump_node.openDump(context, "Childs ");
            for (TOMNode child : childs) {
                dn.dump(context, "Child", child);
            }
        }
    }
}
