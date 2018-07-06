package com.sphenon.engines.generator.gom;

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

import java.util.Stack;

/**
 * Since execution of a GOM tree is intermangled with the execution of
 * some plain java code and the semantics of a single execution unit
 * shall be kept, some synchronisation magic is required.
 *
 * Therefore, a GOM tree is not traversed by simple recursive method
 * invocation, but is controlled by a GOMProcessor instead, which
 * serves as an explicit call stack.
 *
 * By this, GOM tree processing can be paused at any point and resumed
 * respectively.
 */

public interface GOMProcessor {

    /**
     * Processes gom_nodes until java code has to be executed
     */
    public void process (CallContext context);

    /**
     * Continues processing at child with given index and checks (optinally
     * with assert) whether the found child got the correct id
     * @param local_gom_index index of next child to process in current gom node
     * @param gom_id          global unique id of node to verify
     */
    public void process (CallContext context, int local_gom_index, int gom_id);

    /**
     * Pushes a GOMNode on top of the internal call stack
     * This method is used from within GOMNodes to control processing
     * flow through the GOM tree. It is also invoked to initiate processing
     * by pushing the root node on the stack
     */
    public void    push    (CallContext context, GOMNode gom_node, GOMExecutionContext gom_execution_context, GeneratorOutputHandler output_handler, Object... arguments);

    /**
     * Pops the topmost GOMNode from the internal call stack
     * This method is used from within GOMNodes to control processing
     * flow through the GOM tree.
     */
    public void    pop     (CallContext context);

    /**
     * Returns the current node
     */
    public GOMNode getCurrentNode (CallContext context);

    /**
     * Returns the current call stack of nodes
     */
    public Stack<GOMCallStackEntry> getCallStack (CallContext context);

    /**
     * Allows a GOMNode to get it's local variavles from the call stack
     *
     * Please note: a GOMNode should not maintain it's variables
     * as instance members.
     */
    public GOMLocalVariables getLocalVariables (CallContext context);

    /**
     * Allows a GOMNode to store local variables in the call stack
     *
     * Please note: a GOMNode should not maintain it's variables
     * as instance members.
     */
    public void setLocalVariables (CallContext context, GOMLocalVariables local_variables);
}
