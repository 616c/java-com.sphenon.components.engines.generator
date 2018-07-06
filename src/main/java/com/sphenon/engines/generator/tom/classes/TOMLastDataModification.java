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
import com.sphenon.basics.exception.*;
import com.sphenon.basics.notification.*;
import com.sphenon.basics.customary.*;
import com.sphenon.basics.data.*;
import com.sphenon.basics.javacode.*;

import com.sphenon.engines.generator.*;
import com.sphenon.engines.generator.classes.*;
import com.sphenon.engines.generator.tom.*;
import com.sphenon.engines.generator.returncodes.*;

import com.sphenon.basics.expression.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.StringWriter;
import java.io.IOException;

public class TOMLastDataModification extends Class_TOMNode {

    // Configuration ------------------------------------------------------------------

    // Construction -------------------------------------------------------------------

    public TOMLastDataModification(CallContext context, TOMNode parent, String date_expression) throws InvalidTemplateSyntax {
        super(context, parent);
        this.date_expression = date_expression;
    }

    // Attributes ---------------------------------------------------------------------

    protected String    date_expression;

    // Internal -----------------------------------------------------------------------

    // Operations ---------------------------------------------------------------------

    // --------------------------------------------------------------------------------

    public void createJavaCode(CallContext context, Section section, JavaCodeManager java_code_manager, BufferedWriter current_writer, String indent) {
        switch (section) {
            case INIT_SECTION:
                TOMSkeleton skeleton = this.findSuperNode(context, TOMSkeleton.class, null, true);
                skeleton.setLastDataModification(context, this.date_expression);
                break;
        } 
    }
}
