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
import com.sphenon.basics.doclet.*;
import com.sphenon.basics.doclet.classes.*;

import com.sphenon.engines.generator.*;
import com.sphenon.engines.generator.classes.*;
import com.sphenon.engines.generator.tom.*;

import com.sphenon.basics.expression.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.StringWriter;
import java.io.IOException;

public class TOMTemplateComment extends Class_TOMNode {

    // Configuration ------------------------------------------------------------------

    // Construction -------------------------------------------------------------------

    public TOMTemplateComment(CallContext context, TOMNode parent, BufferedReader reader) {
        super(context, parent);
        this.reader = reader;
    }

    // Attributes ---------------------------------------------------------------------

    protected BufferedReader reader;

    static protected RegularExpression msgre     = new RegularExpression("(?s)^\\s*#(?:(?:message\\s)|(?:\\{message\\}))\\s*(.*)");
    static protected RegularExpression docletre  = new RegularExpression("(?s)^\\s*#(?:(?:doc(?:let)?\\s)|(?:\\{doc(?:let)?\\}))\\s*(.*)");
    static protected RegularExpression workletre = new RegularExpression("(?s)^\\s*#(?:(?:work(?:let)?\\s)|(?:\\{work(?:let)?\\}))\\s*(.*)");

    // Internal -----------------------------------------------------------------------

    // Operations ---------------------------------------------------------------------

    // --------------------------------------------------------------------------------

    public void createJavaCode(CallContext context, Section section, JavaCodeManager java_code_manager, BufferedWriter current_writer, String indent) {

        try {
            switch (section) {
                case INIT_SECTION:
                    StringWriter writer = new StringWriter();
                    int c;
                    while ((c = this.reader.read()) != -1) {
                        writer.write((char)c);
                    }
                    String comment = writer.toString();
                    String[] matches;
                    if ((matches = msgre.tryGetMatches(context, comment)) != null) {
                        System.err.println(matches[0]);
                    }
                    if ((matches = docletre.tryGetMatches(context, comment)) != null) {
                        TOMSkeleton skeleton = this.findSuperNode(context, TOMSkeleton.class, null, true);
                        skeleton.addDoclet(context, matches[0]);
                    }
                    if ((matches = workletre.tryGetMatches(context, comment)) != null) {
                        TOMSkeleton skeleton = this.findSuperNode(context, TOMSkeleton.class, null, true);
                        skeleton.addWorklet(context, matches[0]);
                    }
                    break;
            } 
        } catch (IOException ioe) {
            CustomaryContext.create((Context)context).throwEnvironmentFailure(context, ioe, "Could not write to java source file/stream (for template '%(template)')", "template", this.getRootNode(context).getTemplate(context).getFullClassName(context));
            throw (ExceptionEnvironmentFailure) null; // compiler insists
        }
    }
}
