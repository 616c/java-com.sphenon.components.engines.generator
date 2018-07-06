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

public class GOMPartition extends Class_GOMNode {

    // Configuration ------------------------------------------------------------------

    // Construction -------------------------------------------------------------------

    public GOMPartition(CallContext context, GOMNode parent, int id) {
        super(context, parent, id);
    }

    // Attributes ---------------------------------------------------------------------

    protected String partition_name;

    public String getPartitionName (CallContext context) {
        return this.partition_name;
    }

    public void setPartitionName (CallContext context, String partition_name) {
        this.partition_name = partition_name;
    }

    protected boolean do_not_modify;

    public boolean getDoNotModify (CallContext context) {
        return this.do_not_modify;
    }

    public void setDoNotModify (CallContext context, boolean do_not_modify) {
        this.do_not_modify = do_not_modify;
    }

    protected boolean close_at_partition_end;

    public boolean getCloseAtPartitionEnd (CallContext context) {
        return this.close_at_partition_end;
    }

    public void setCloseAtPartitionEnd (CallContext context, boolean close_at_partition_end) {
        this.close_at_partition_end = close_at_partition_end;
    }

    // Internal -----------------------------------------------------------------------

    protected class MyLocalVariables extends Class_GOMLocalVariables {
        public String previous_partition;
        public boolean got_name;
    }

    // Operations ---------------------------------------------------------------------

    // --------------------------------------------------------------------------------

    public boolean generate (CallContext context, GOMProcessor gom_processor, GOMExecutionContext gom_execution_context, GeneratorOutputHandler output_handler, Object... arguments) {

        MyLocalVariables local = (MyLocalVariables) gom_processor.getLocalVariables(context);
        if (local == null) {
            local = new MyLocalVariables();
            gom_processor.setLocalVariables(context, local);
            return false;
        } else if (local.got_name == false) {
            local.previous_partition = output_handler.redirectDefaultWriter(context, partition_name, do_not_modify);
            local.got_name = true;
        } else {
            local.index++;
        }

        if (local.index < this.getChildNodes(context).size()) {
            gom_processor.push(context, this.getChildNodes(context).elementAt(local.index), gom_execution_context, output_handler, arguments);
        } else {
            gom_processor.setLocalVariables(context, null);
            gom_processor.pop(context);
            if (close_at_partition_end) {
                output_handler.closeCurrent(context);
            }
            output_handler.redirectDefaultWriter(context, local.previous_partition);
        }
        return true;
    }
}
