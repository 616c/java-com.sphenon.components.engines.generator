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

import java.io.BufferedWriter;
import java.util.Vector;

public interface GOMNode {
    public int             getId         (CallContext context);
    public GOMNode         getRootNode   (CallContext context);
    public GOMNode         getParentNode (CallContext context);
    public Vector<GOMNode> getChildNodes (CallContext context);

    /**
     * Causes this GOMNode to perform it's execution.
     *
     * Please note specifically: this method is to be used in
     * conjunction with a GOMProcessor, it does not just generate
     * the complete result, as might be expected.
     *
     * @return true, if the GOMProcessor shall continue execution, false otherwise
     */
    public boolean generate (CallContext context, GOMProcessor gom_processor, GOMExecutionContext gom_execution_context, GeneratorOutputHandler output_handler, Object... arguments);

    /**
     * Used by generated java code to adjust the processing to the node
     * following the current code in the template
     */
    public void seek (CallContext context, GOMProcessor gom_processor, int child_index);

    /**
       Dumps the whole GOM Tree as SELF_DIAGNOSTICS messages.
    */
    public void dumpGOMTree(CallContext context);

    /**
       Used internally by dumper to pass indentation.
    */
    public void dumpGOMTree(CallContext context, String indent);
}
