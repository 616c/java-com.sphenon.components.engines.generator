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

public class GOMCallStackEntry {

    public GOMCallStackEntry (GOMNode gom_node, GOMExecutionContext gom_execution_context, GeneratorOutputHandler output_handler, Object[] arguments) {
        this.gom_node              = gom_node;
        this.gom_execution_context = gom_execution_context;
        this.output_handler        = output_handler;
        this.arguments             = arguments;
        this.local_variables       = null;
    }

    public GOMNode                gom_node;

    public GOMExecutionContext    gom_execution_context;

    public GeneratorOutputHandler output_handler;

    public Object[]               arguments;

    public GOMLocalVariables      local_variables;
}
