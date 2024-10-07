package com.sphenon.engines.generator.tchandler.classes;

/****************************************************************************
  Copyright 2001-2024 Sphenon GmbH

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
import com.sphenon.engines.generator.tom.*;
import com.sphenon.engines.generator.tom.classes.*;
import com.sphenon.engines.generator.tchandler.*;
import com.sphenon.engines.generator.returncodes.*;

import java.io.BufferedReader;

public class TCHJavaCodeInTemplateCode implements TCHandler {

    protected TCHTemplateCode template_code_handler;

    public TCHTemplateCode getTemplateCodeHandler (CallContext context) {
        return this.template_code_handler;
    }

    public void setTemplateCodeHandler (CallContext context, TCHTemplateCode template_code_handler) {
        this.template_code_handler = template_code_handler;
    }

    public TCHJavaCodeInTemplateCode (CallContext context) {
    }

    public TOMNode handle(CallContext context, TCEvent event, TOMNode current_node, BufferedReader reader) throws InvalidTemplateSyntax {

        this.template_code_handler.handleProcessedJavaCode(context, reader);
        
        return current_node;
    }
}
