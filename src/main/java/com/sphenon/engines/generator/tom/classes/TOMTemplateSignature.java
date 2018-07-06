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
import com.sphenon.engines.generator.tchandler.classes.*;
import com.sphenon.engines.generator.returncodes.*;

import com.sphenon.basics.expression.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.StringWriter;
import java.io.IOException;

import java.util.Vector;

public class TOMTemplateSignature extends Class_TOMNode {

    // Configuration ------------------------------------------------------------------

    // Construction -------------------------------------------------------------------

    public TOMTemplateSignature(CallContext context, TOMNode parent, Vector<TCodeArgument> signature) throws InvalidTemplateSyntax {
        super(context, parent);
        this.signature = signature;
        while (parent != null && ! (parent instanceof TOMSkeleton)) {
            parent = parent.getParentNode(context);
        }
        if (parent == null) {
            CustomaryContext.create((Context)context).throwConfigurationError(context, "Cannot find TOMSkeleton in TOM tree - signature cannot be applied");
            throw (ExceptionConfigurationError) null; // compiler insists
        }
        ((TOMSkeleton)parent).setSignature(context, new Class_Signature(context, signature));
    }

    // Attributes ---------------------------------------------------------------------

    protected Vector<TCodeArgument> signature;

    // Internal -----------------------------------------------------------------------

    // Operations ---------------------------------------------------------------------

    // --------------------------------------------------------------------------------

    public void createJavaCode(CallContext context, Section section, JavaCodeManager java_code_manager, BufferedWriter current_writer, String indent) {

    }
}
