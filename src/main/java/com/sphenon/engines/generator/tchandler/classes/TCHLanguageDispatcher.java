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
import com.sphenon.basics.system.*;

import com.sphenon.engines.generator.*;
import com.sphenon.engines.generator.tom.*;
import com.sphenon.engines.generator.tom.classes.*;
import com.sphenon.engines.generator.tchandler.*;
import com.sphenon.engines.generator.returncodes.*;

import java.io.BufferedReader;
import java.io.StringWriter;
import java.io.StringReader;
import java.io.IOException;

public class TCHLanguageDispatcher implements TCHandler {

    public TCHLanguageDispatcher (CallContext context) {
    }
    
    protected TCHandler text_handler;

    public TCHandler getTextHandler (CallContext context) {
        return this.text_handler;
    }

    public void setTextHandler (CallContext context, TCHandler text_handler) {
        this.text_handler = text_handler;
    }

    protected TCHandler java_code_handler;

    public TCHandler getJavaCodeHandler (CallContext context) {
        return this.java_code_handler;
    }

    public void setJavaCodeHandler (CallContext context, TCHandler java_code_handler) {
        this.java_code_handler = java_code_handler;
    }

    protected TCHandler java_expression_handler;

    public TCHandler getJavaExpressionHandler (CallContext context) {
        return this.java_expression_handler;
    }

    public void setJavaExpressionHandler (CallContext context, TCHandler java_expression_handler) {
        this.java_expression_handler = java_expression_handler;
    }

    protected TCHandler template_comment_handler;

    public TCHandler getTemplateCommentHandler (CallContext context) {
        return this.template_comment_handler;
    }

    public void setTemplateCommentHandler (CallContext context, TCHandler template_comment_handler) {
        this.template_comment_handler = template_comment_handler;
    }

    protected TCHandler template_code_handler;

    public TCHandler getTemplateCodeHandler (CallContext context) {
        return this.template_code_handler;
    }

    public void setTemplateCodeHandler (CallContext context, TCHandler template_code_handler) {
        this.template_code_handler = template_code_handler;
    }

    protected TCHandler tag_handler;

    public TCHandler getTagHandler (CallContext context) {
        return this.tag_handler;
    }

    public void setTagHandler (CallContext context, TCHandler tag_handler) {
        this.tag_handler = tag_handler;
    }

    protected TCHandlerPP pp_tag_handler;

    public TCHandlerPP getPPTagHandler (CallContext context) {
        return this.pp_tag_handler;
    }

    public void setPPTagHandler (CallContext context, TCHandlerPP pp_tag_handler) {
        this.pp_tag_handler = pp_tag_handler;
    }

    protected boolean leave_text_unprocessed_before_tags;

    public boolean getLeaveTextUnprocessedBeforeTags (CallContext context) {
        return this.leave_text_unprocessed_before_tags;
    }

    public boolean defaultLeaveTextUnprocessedBeforeTags (CallContext context) {
        return false;
    }

    public void setLeaveTextUnprocessedBeforeTags (CallContext context, boolean leave_text_unprocessed_before_tags) {
        this.leave_text_unprocessed_before_tags = leave_text_unprocessed_before_tags;
    }

    public TOMNode handle(CallContext context, TCEvent event, TOMNode current_node, BufferedReader reader) throws InvalidTemplateSyntax{
        switch (event) {
            case TEMPLATE_BEGIN:
                break;
            case TEMPLATE_SOURCE:
                TeeBufferedReader tbr = new TeeBufferedReader(reader, null);
                LanguageDispatcher language_dispatcher = new LanguageDispatcher (context, tbr, this);
                try {
                    current_node = language_dispatcher.ASCII(context, current_node);
                } catch (ParseException pe) {
                    try { tbr.close(); } catch(IOException ioe) {}
                    InvalidTemplateSyntax.createAndThrow(context, pe, "Invalid Template Syntax (Basic Language Structure) at %(position) : %(data)", "position", language_dispatcher.getPosition(context), "data", tbr.getString());
                    throw (InvalidTemplateSyntax) null;
                } catch (TokenMgrError tme) {
                    try { tbr.close(); } catch(IOException ioe) {}
                    InvalidTemplateSyntax.createAndThrow(context, tme, "Invalid Template Syntax (Basic Language Structure) at %(position) : %(data)", "position", language_dispatcher.getPosition(context), "data", tbr.getString());
                    throw (InvalidTemplateSyntax) null;
                } catch (InvalidTemplateSyntax its) {
                    try { tbr.close(); } catch(IOException ioe) {}
                    InvalidTemplateSyntax.createAndThrow(context, its, "Invalid Template Syntax (Basic Language Structure) at  %(position) : %(data)", "position", language_dispatcher.getPosition(context), "data", tbr.getString());
                    throw (InvalidTemplateSyntax) null;
                }
                break;
            case TEMPLATE_END:
                current_node = this.processBufferedText(context, current_node);
                break;
            default:
                current_node = null;
                break;
        }

        return current_node;
    }

    protected StringBuffer buffered_text;

    public TOMNode handleText(CallContext context, TOMNode current_node, String text) throws InvalidTemplateSyntax {
        if (text != null && text.length() != 0) {
            if (this.buffered_text == null) {
                this.buffered_text = new StringBuffer();
            }
            this.buffered_text.append(text);
        }
        return current_node;
    }

    public TOMNode processBufferedText(CallContext context, TOMNode current_node) throws InvalidTemplateSyntax {
        if (this.buffered_text != null && this.buffered_text.length() != 0) {
            StringReader string_reader = new StringReader(this.buffered_text.toString());
            BufferedReader buffered_reader = new BufferedReader(string_reader);
            current_node = this.text_handler.handle(context, TCEvent.TEXT, current_node, buffered_reader);
            this.buffered_text.setLength(0);
        }
        return current_node;
    }

    public TOMNode handleJavaCodeGeneration(CallContext context, TOMNode current_node, String java_code) throws InvalidTemplateSyntax {
        current_node = this.processBufferedText(context, current_node);
        if (java_code != null && java_code.length() != 0) {
            StringReader string_reader = new StringReader(java_code);
            BufferedReader buffered_reader = new BufferedReader(string_reader);
            current_node = this.java_code_handler.handle(context, TCEvent.JAVA_CODE_GENERATION, current_node, buffered_reader);
        }
        return current_node;
    }

    public TOMNode handleJavaCodeGenerationEmbeddedCode(CallContext context, TOMNode current_node, String java_code, String embedding_syntax_id) throws InvalidTemplateSyntax {
        current_node = this.processBufferedText(context, current_node);
        if (java_code != null && java_code.length() != 0) {
            StringReader string_reader = new StringReader(java_code);
            BufferedReader buffered_reader = new BufferedReader(string_reader);
            current_node = this.java_code_handler.handle(context, TCEvent.JAVA_CODE_GENERATION_EMBEDDED_CODE, current_node, buffered_reader);
        }
        return current_node;
    }

    public TOMNode handleJavaCodeGenerationContinuation(CallContext context, TOMNode current_node, String java_code) throws InvalidTemplateSyntax {
        current_node = this.processBufferedText(context, current_node);
        if (java_code != null && java_code.length() != 0) {
            StringReader string_reader = new StringReader(java_code);
            BufferedReader buffered_reader = new BufferedReader(string_reader);
            current_node = this.java_code_handler.handle(context, TCEvent.JAVA_CODE_GENERATION_CONTINUATION, current_node, buffered_reader);
        }
        return current_node;
    }

    public TOMNode handleJavaCodeClass(CallContext context, TOMNode current_node, String java_code) throws InvalidTemplateSyntax {
        current_node = this.processBufferedText(context, current_node);
        if (java_code != null && java_code.length() != 0) {
            StringReader string_reader = new StringReader(java_code);
            BufferedReader buffered_reader = new BufferedReader(string_reader);
            current_node = this.java_code_handler.handle(context, TCEvent.JAVA_CODE_CLASS, current_node, buffered_reader);
        }
        return current_node;
    }

    public TOMNode handleJavaCodeImport(CallContext context, TOMNode current_node, String java_code) throws InvalidTemplateSyntax {
        current_node = this.processBufferedText(context, current_node);
        if (java_code != null && java_code.length() != 0) {
            StringReader string_reader = new StringReader(java_code);
            BufferedReader buffered_reader = new BufferedReader(string_reader);
            current_node = this.java_code_handler.handle(context, TCEvent.JAVA_CODE_IMPORT, current_node, buffered_reader);
        }
        return current_node;
    }

    public TOMNode handleJavaExpression(CallContext context, TOMNode current_node, String java_expression) throws InvalidTemplateSyntax {
        current_node = this.processBufferedText(context, current_node);
        if (java_expression != null && java_expression.length() != 0) {
            StringReader string_reader = new StringReader(java_expression);
            BufferedReader buffered_reader = new BufferedReader(string_reader);
            current_node = this.java_expression_handler.handle(context, TCEvent.JAVA_EXPRESSION, current_node, buffered_reader);
        }
        return current_node;
    }

    public TOMNode handleTemplateComment(CallContext context, TOMNode current_node, String template_comment) throws InvalidTemplateSyntax {
        current_node = this.processBufferedText(context, current_node);
        if (template_comment != null && template_comment.length() != 0) {
            StringReader string_reader = new StringReader(template_comment);
            BufferedReader buffered_reader = new BufferedReader(string_reader);
            current_node = this.template_comment_handler.handle(context, TCEvent.TEMPLATE_COMMENT, current_node, buffered_reader);
        }
        return current_node;
    }

    public TOMNode handleTemplateCode(CallContext context, TOMNode current_node, String template_code) throws InvalidTemplateSyntax {
        current_node = this.processBufferedText(context, current_node);
        if (template_code != null && template_code.length() != 0) {
            StringReader string_reader = new StringReader(template_code);
            BufferedReader buffered_reader = new BufferedReader(string_reader);
            current_node = this.template_code_handler.handle(context, TCEvent.TEMPLATE_CODE, current_node, buffered_reader);
        }
        return current_node;
    }

    public TOMNode handleTag(CallContext context, TCEvent event, TOMNode current_node, String tag) throws InvalidTemplateSyntax {
        if ( ! this.leave_text_unprocessed_before_tags) {
            current_node = this.processBufferedText(context, current_node);
        }
        if (tag != null && tag.length() != 0) {
            StringReader string_reader = new StringReader(tag);
            BufferedReader buffered_reader = new BufferedReader(string_reader);
            current_node = this.tag_handler.handle(context, event, current_node, buffered_reader);
        }
        return current_node;
    }

    public void handlePPTag(CallContext context, TCEvent event, TOMNode current_node, StringBuffer out, String tag) throws InvalidTemplateSyntax {
        if (tag != null && tag.length() != 0) {
            StringReader string_reader = new StringReader(tag);
            BufferedReader buffered_reader = new BufferedReader(string_reader);
            this.pp_tag_handler.handle(context, event, current_node, buffered_reader, out);
        }
    }
}
