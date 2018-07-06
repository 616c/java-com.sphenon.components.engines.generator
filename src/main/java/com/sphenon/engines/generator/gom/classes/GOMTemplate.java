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

import java.util.Stack;

public class GOMTemplate extends Class_GOMNode {

    // Configuration ------------------------------------------------------------------

    // Construction -------------------------------------------------------------------

    public GOMTemplate(CallContext context, int id) {
        super(context, null, id);
    }

    public GOMTemplate(CallContext context, GOMNode parent, int id) {
        super(context, parent, id);
    }

    // Attributes ---------------------------------------------------------------------

    // Internal -----------------------------------------------------------------------

    protected class MyLocalVariables extends Class_GOMLocalVariables {
    }

    // Operations ---------------------------------------------------------------------

    // --------------------------------------------------------------------------------

    public boolean generate (CallContext context, GOMProcessor gom_processor, GOMExecutionContext gom_execution_context, GeneratorOutputHandler output_handler, Object... arguments) {

        Stack<GOMCallStackEntry> stack = gom_processor.getCallStack(context);
        if (stack.size() < 2 || (stack.elementAt(stack.size()-2).gom_node instanceof GOMInsert) == false) {
            gom_processor.setLocalVariables(context, null);
            gom_processor.pop(context);
            return false;
        }

        MyLocalVariables local = (MyLocalVariables) gom_processor.getLocalVariables(context);
        if (local == null) {
            local = new MyLocalVariables();
            gom_processor.setLocalVariables(context, local);
        } else {
            local.index++;
        }

        if (local.index < this.getChildNodes(context).size()) {
            gom_processor.push(context, this.getChildNodes(context).elementAt(local.index), gom_execution_context, output_handler, arguments);
        } else {
            gom_processor.setLocalVariables(context, null);
            gom_processor.pop(context);
        }
        return true;
    }
}
