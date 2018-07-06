package com.sphenon.engines.generator.tom;

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
import com.sphenon.basics.javacode.*;

import com.sphenon.engines.generator.*;
import com.sphenon.engines.generator.classes.*;
import com.sphenon.engines.generator.tom.classes.*;

import java.io.BufferedWriter;
import java.util.Vector;

public interface TOMNode {
    public TOMRoot          getRootNode   (CallContext context);
    public TOMNode          getParentNode (CallContext context);
    public Vector<TOMNode>  getChildNodes (CallContext context);

    public<NodeClass extends TOMNode> NodeClass findSubNode        (CallContext context, Class<NodeClass> node_class);
    public<NodeClass extends TOMNode> NodeClass findSubNode        (CallContext context, Class<NodeClass> node_class, TOMCondition tom_condition);
    public<NodeClass extends TOMNode> NodeClass findSubNode        (CallContext context, Class<NodeClass> node_class, TOMCondition tom_condition, boolean mandatory);

    public<NodeClass extends TOMNode> NodeClass findSuperNode      (CallContext context, Class<NodeClass> node_class);
    public<NodeClass extends TOMNode> NodeClass findSuperNode      (CallContext context, Class<NodeClass> node_class, TOMCondition tom_condition);
    public<NodeClass extends TOMNode> NodeClass findSuperNode      (CallContext context, Class<NodeClass> node_class, TOMCondition tom_condition, boolean mandatory);

    public<NodeClass extends TOMNode> NodeClass findPreceedingNode (CallContext context, Class<NodeClass> node_class);
    public<NodeClass extends TOMNode> NodeClass findPreceedingNode (CallContext context, Class<NodeClass> node_class, TOMCondition tom_condition);
    public<NodeClass extends TOMNode> NodeClass findPreceedingNode (CallContext context, Class<NodeClass> node_class, TOMCondition tom_condition, boolean mandatory);
    public<NodeClass extends TOMNode> NodeClass findPreceedingNode (CallContext context, Class<NodeClass> node_class, TOMCondition tom_condition, boolean mandatory, boolean include_start_node);

    public TOMProperties    getProperties(CallContext context);
    public Recoding         getRecoding(CallContext context);
    public Recoding         getTextRecoding(CallContext context);

    public enum Section {
        INIT_SECTION,
        NO_SECTION,
        IMPORT_SECTION,
        ATTRIBUTE_SECTION,
        STATIC_INIT_CODE_SECTION,
        METHOD_SECTION,
        GOM_BUILDER_SECTION,
        GOM_BUILDER_DECLARATION_SECTION,
        GOM_BUILDER_LINKING_SECTION,
        GENERATOR_CODE_SECTION,
        DATA_SECTION
    };

    public void createJavaCode(CallContext context, Section section, JavaCodeManager java_code_manager, BufferedWriter current_writer, String indent);
}
