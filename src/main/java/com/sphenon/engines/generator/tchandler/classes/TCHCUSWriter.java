package com.sphenon.engines.generator.tchandler.classes;

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
import com.sphenon.engines.generator.tom.*;
import com.sphenon.engines.generator.tom.classes.*;
import com.sphenon.engines.generator.tchandler.*;
import com.sphenon.engines.generator.returncodes.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

public class TCHCUSWriter implements TCHandler {

    public TCHCUSWriter (CallContext context) {
    }

    protected BufferedWriter writer;

    public BufferedWriter getWriter (CallContext context) {
        return this.writer;
    }

    public void setWriter (CallContext context, BufferedWriter writer) {
        this.writer = writer;
    }

    public TOMNode handle(CallContext context, TCEvent event, TOMNode current_node, BufferedReader reader) throws InvalidTemplateSyntax {

        try {
            switch (event) {
                case TEMPLATE_BEGIN:
                    break;
                case TEMPLATE_SOURCE:
                    break;
                case TEMPLATE_END:
                    break;
                case TEMPLATE_CODE:
                    writer.write("\u228F");
                    break;
                case TEMPLATE_COMMENT:
                    writer.write("\u22B0");
                    break;
                case JAVA_CODE_IMPORT:
                    writer.write("\u25BC");
                    break;
                case JAVA_CODE_CLASS:
                    writer.write("\u25B2");
                    break;
                case JAVA_CODE_GENERATION:
                    writer.write("\u00AB");
                    break;
                case JAVA_EXPRESSION:
                    writer.write("\u25C2");
                    break;
                case TEXT:
                    break;
                default:
                    InvalidTemplateSyntax.createAndThrow(context, "Internal Template Syntax error: cannot handle '%(event)'", "event", event);
                    throw (InvalidTemplateSyntax) null;
            }
            
            int c;
            while ((c = reader.read()) != -1) { writer.write((char) c); }

            switch (event) {
                case TEMPLATE_BEGIN:
                    break;
                case TEMPLATE_SOURCE:
                    break;
                case TEMPLATE_END:
                    break;
                case TEMPLATE_CODE:
                    writer.write("\u2290");
                    break;
                case TEMPLATE_COMMENT:
                    writer.write("\u22B1");
                    break;
                case JAVA_CODE_IMPORT:
                    writer.write("\u25BC");
                    break;
                case JAVA_CODE_CLASS:
                    writer.write("\u25B2");
                    break;
                case JAVA_CODE_GENERATION:
                    writer.write("\u00BB");
                    break;
                case JAVA_EXPRESSION:
                    writer.write("\u25B8");
                    break;
                case TEXT:
                    break;
                default:
                    InvalidTemplateSyntax.createAndThrow(context, "Internal Template Syntax error: cannot handle '%(event)'", "event", event);
                    throw (InvalidTemplateSyntax) null;
            }

        } catch (IOException ioe) {
            CustomaryContext.create((Context)context).throwEnvironmentFailure(context, ioe, "Writing in JSPStyleWriter failed");
            throw (ExceptionEnvironmentFailure) null; // compiler insists
        }

        return current_node;
    }
}
