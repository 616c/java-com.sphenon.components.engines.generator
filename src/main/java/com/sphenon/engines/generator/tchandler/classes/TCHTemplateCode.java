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

import java.util.Vector;

public class TCHTemplateCode implements TCHandler {

    public TCHTemplateCode (CallContext context) {
    }

    public TOMNode handle(CallContext context, TCEvent event, TOMNode current_node, BufferedReader reader) throws InvalidTemplateSyntax{

        TemplateCode template_code = new TemplateCode (context, reader, this);
        try {
            current_node = template_code.TemplateCode(context, current_node);
        } catch (ParseException pe) {
            InvalidTemplateSyntax.createAndThrow(context, pe, "Invalid Template Syntax (Template Code) at " + template_code.getPosition(context));
            throw (InvalidTemplateSyntax) null;
        } catch (TokenMgrError tme) {
            InvalidTemplateSyntax.createAndThrow(context, tme, "Invalid Template Syntax (Template Code) at " + template_code.getPosition(context));
            throw (InvalidTemplateSyntax) null;
        } catch (InvalidTemplateSyntax its) {
            InvalidTemplateSyntax.createAndThrow(context, its, "Invalid Template Syntax (Template Code) at " + template_code.getPosition(context));
            throw (InvalidTemplateSyntax) null;
        }

        return current_node;
    }

    public TOMNode handleSignature(CallContext context, TOMNode current_node, Vector signature) throws InvalidTemplateSyntax {
        new TOMTemplateSignature(context, current_node, (Vector<TCodeArgument>) signature);
        return current_node;
    }

    public TOMNode handleName(CallContext context, TOMNode current_node, String name_expression) throws InvalidTemplateSyntax {
        new TOMTemplateName(context, current_node, name_expression);
        return current_node;
    }

    public TOMNode handleLastDataModification(CallContext context, TOMNode current_node, String date_expression) throws InvalidTemplateSyntax {
        new TOMLastDataModification(context, current_node, date_expression);
        return current_node;
    }

    public TOMNode handleLocatorTarget(CallContext context, TOMNode current_node, String id, Vector arguments) throws InvalidTemplateSyntax {
        new TOMTemplateLocatorTarget(context, current_node, id, (Vector<TCodeArgument>) arguments);
        return current_node;
    }

    public TOMNode handleTemplateBegin(CallContext context, TOMNode current_node, String template_name, Vector signature) throws InvalidTemplateSyntax {
        return new TOMTemplateTemplate(context, current_node, template_name, (Vector<TCodeArgument>) signature);
    }

    public TOMNode handleTemplateEnd(CallContext context, TOMNode current_node) throws InvalidTemplateSyntax {
        if (current_node instanceof TOMTemplateTemplate) {
            return current_node.getParentNode(context);
        }
        InvalidTemplateSyntax.createAndThrow(context, "Template end does not match to a template begin node, but to a '%(node)'", "node", current_node);
        throw (InvalidTemplateSyntax) null;
    }

    public TOMNode handlePartitionBegin(CallContext context, TOMNode current_node, String name_expression, String do_not_modify, String close_at_partition_end) throws InvalidTemplateSyntax {
        return new TOMTemplatePartition(context, current_node, name_expression, do_not_modify, close_at_partition_end);
    }

    public TOMNode handlePartitionEnd(CallContext context, TOMNode current_node) throws InvalidTemplateSyntax {
        if (current_node instanceof TOMTemplatePartition) {
            return current_node.getParentNode(context);
        }
        InvalidTemplateSyntax.createAndThrow(context, "Partition end does not match to a partition begin node, but to a '%(node)'", "node", current_node);
        throw (InvalidTemplateSyntax) null;
    }

    public TOMNode handleInsert(CallContext context, TOMNode current_node, String generator_name, String template_name, String template_name_code, String arguments) throws InvalidTemplateSyntax {
        if (template_name != null && template_name.length() != 0) {
            if (template_name.matches("^this\\..*")) {
                new TOMTemplateInsert(context, current_node, template_name.substring(5), arguments, true);
            } else if (template_name.matches("^super\\..*")) {
                new TOMTemplateInsert(context, current_node, template_name.substring(6), arguments, false);
            } else {
                new TOMTemplateInsertExternal(context, current_node, generator_name, "\"" + template_name + "\"", arguments);
            }
        } else {
            new TOMTemplateInsertExternal(context, current_node, generator_name, template_name_code, arguments);
        }
        return current_node;
    }

    public TOMNode handleBase(CallContext context, TOMNode current_node, String base_name) throws InvalidTemplateSyntax {
        new TOMTemplateBase(context, current_node, base_name);
        return current_node;
    }

    public TOMNode handleInterfaces(CallContext context, TOMNode current_node, String interfaces) throws InvalidTemplateSyntax {
        new TOMTemplateInterfaces(context, current_node, interfaces);
        return current_node;
    }

    public TOMNode handlePolymorphic(CallContext context, TOMNode current_node, String polymorphic) throws InvalidTemplateSyntax {
        new TOMTemplatePolymorphic(context, current_node, polymorphic);
        return current_node;
    }

    public TOMNode handleOverride(CallContext context, TOMNode current_node) throws InvalidTemplateSyntax {
        new TOMTemplateOverride(context, current_node);
        return current_node;
    }

    public TOMNode handleRequirement(CallContext context, TOMNode current_node, String template_name, String arguments) throws InvalidTemplateSyntax {
        new TOMTemplateRequirement(context, current_node, template_name, arguments);
        return current_node;
    }

    public TOMNode handleRecodingBegin(CallContext context, TOMNode current_node, Vector recoding, String applies_to) throws InvalidTemplateSyntax {
        if (recoding == null || recoding.size() == 0) {
            InvalidTemplateSyntax.createAndThrow(context, "Recoding is empty");
            throw (InvalidTemplateSyntax) null;
        }

        current_node = new TOMPlain(context, current_node);

        if (applies_to == null || applies_to.matches(".*[xX].*")) {
            current_node.getProperties(context).setRecoding(context, new Recoding(context, (Vector<String[]>) recoding));
        }
        if (applies_to == null || applies_to.matches(".*[tT].*")) {
            current_node.getProperties(context).setTextRecoding(context, new Recoding(context, (Vector<String[]>) recoding));
        }

        return current_node;
    }

    public TOMNode handleRecodingEnd(CallContext context, TOMNode current_node) throws InvalidTemplateSyntax {
        if (current_node instanceof TOMPlain) {
            return current_node.getParentNode(context);
        }
        InvalidTemplateSyntax.createAndThrow(context, "Recoding end does not match to a recoding begin node, but to a '%(node)'", "node", current_node);
        throw (InvalidTemplateSyntax) null;
    }

}
