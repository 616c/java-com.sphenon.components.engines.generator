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
import java.io.StringWriter;
import java.io.StringReader;
import java.io.IOException;

public class TCHCompactUnicodeSyntax2ASCII implements TCHandler {

    public TCHCompactUnicodeSyntax2ASCII (CallContext context) {
    }
    
    protected TCHandler ascii_source_handler;

    public TCHandler getASCIISourceHandler (CallContext context) {
        return this.ascii_source_handler;
    }

    public void setASCIISourceHandler (CallContext context, TCHandler ascii_source_handler) {
        this.ascii_source_handler = ascii_source_handler;
    }

    protected boolean java_template_characters;

    public boolean getJavaTemplateCharacters (CallContext context) {
        return this.java_template_characters;
    }

    public boolean defaultJavaTemplateCharacters (CallContext context) {
        return false;
    }

    public void setJavaTemplateCharacters (CallContext context, boolean java_template_characters) {
        this.java_template_characters = java_template_characters;
    }

    protected boolean model_template_characters;

    public boolean getModelTemplateCharacters (CallContext context) {
        return this.model_template_characters;
    }

    public boolean defaultModelTemplateCharacters (CallContext context) {
        return false;
    }

    public void setModelTemplateCharacters (CallContext context, boolean model_template_characters) {
        this.model_template_characters = model_template_characters;
    }

    public TOMNode handle(CallContext context, TCEvent event, TOMNode current_node, BufferedReader reader) throws InvalidTemplateSyntax{
        switch (event) {
            case TEMPLATE_BEGIN:
                current_node = this.ascii_source_handler.handle(context, event, current_node, null);
                break;
            case TEMPLATE_SOURCE:
                StringWriter writer = new StringWriter();

                CUS2ASCII cus2ascii = new CUS2ASCII (context, reader, writer, this.java_template_characters, this.model_template_characters);
                try {
                    cus2ascii.CUS();
                } catch (IOException ioe) {
                    CustomaryContext.create((Context)context).throwEnvironmentFailure(context, ioe, "Could not parse CUS");
                    throw (ExceptionEnvironmentFailure) null; // compiler insists
                } catch (ParseException pe) {
                    InvalidTemplateSyntax.createAndThrow(context, pe, "Invalid CUS (Compact Unicode Syntax) at " + cus2ascii.getPosition(context));
                    throw (InvalidTemplateSyntax) null;
                } catch (TokenMgrError tme) {
                    InvalidTemplateSyntax.createAndThrow(context, tme, "Invalid CUS (Compact Unicode Syntax) at " + cus2ascii.getPosition(context));
                    throw (InvalidTemplateSyntax) null;
                }
                
                String ts = writer.toString();
                StringReader string_reader = new StringReader(ts);
                BufferedReader buffered_reader = new BufferedReader(string_reader);
                //System.err.println("Len of TCode: " + ts.length());
                //System.err.println("TCode: " + ts);
                current_node = this.ascii_source_handler.handle(context, event, current_node, buffered_reader);
                break;
            case TEMPLATE_END:
                current_node = this.ascii_source_handler.handle(context, event, current_node, null);
                break;
            default:
                current_node = null;
                break;
        }

        return current_node;
    }
}
