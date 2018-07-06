package com.sphenon.engines.generator.tchandler;

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

import java.util.Vector;

/**
   Instances of this class can be plugged in into the Generator. They provide
   customized behaviour for the template language.
 */
public interface TagHandler {

    /**
       This method is invoked if a begin tag was found. The syntax of such a
       tag is <@ tagname(arg1, arg2, ...) @> (in JSP notation; in UCS notation
       there are two special unicode characters avaible)

       @param event                Identifies the current processing context,
                                   which is one of:
                                   TAG, TEMPLATE_CODE_TAG, JAVA_CODE_TAG,
                                   JAVA_EXPRESSION_TAG. TAG_TAG.
       @param current_node         The current TOMNode instance of the created
                                   TOMTree (TOM = Template Object Model). The
                                   TagHandler can add nodes to the tree
       @param tag_name             The tagname from the above expression
       @param arguments            The arguments (arg1 etc.) from the above expression
       @param ascii_source_handler If provided, the TagHandler can write
                                   template code in JSP syntax into this
                                   argument as an alternative to manipulating
                                   the TOMTree directly
       @param out                  If non null, this TagHandler must not
                                   modify the TOMTree or write to the
                                   ascii_source_handler; instead, it can only
                                   write to this StringBuffer argument, which
                                   is inserted into the code processed by an
                                   invoking handler. The parameter is null if
                                   and only if the event argument equals to TAG.
       @return The old or new current TOMNode in case of event is TAG, or null
               in all other cases (i.e. the returned value is simply ignored, then)
     */
    public TOMNode handleTagBegin(CallContext context, TCEvent event, TOMNode current_node, String tag_name, Vector<String>  arguments, TCHandler ascii_source_handler, StringBuffer out) throws InvalidTemplateSyntax;

    /**
       This method is invoked if an end tag was found. The syntax of such a
       tag is <@ / tagname(arg1, arg2, ...) @> (in JSP notation; in UCS notation
       there are two special unicode characters avaible)

       @param event                Identifies the current processing context,
                                   which is one of:
                                   TAG, TEMPLATE_CODE_TAG, JAVA_CODE_TAG,
                                   JAVA_EXPRESSION_TAG.
       @param current_node         The current TOMNode instance of the created
                                   TOMTree (TOM = Template Object Model). The
                                   TagHandler can add nodes to the tree
       @param tag_name             The tagname from the above expression
       @param arguments            The arguments (arg1 etc.) from the above expression
       @param ascii_source_handler If provided, the TagHandler can write
                                   template code in JSP syntax into this
                                   argument as an alternative to manipulating
                                   the TOMTree directly
       @param out                  If non null, this TagHandler must not
                                   modify the TOMTree or write to the
                                   ascii_source_handler; instead, it can only
                                   write to this StringBuffer argument, which
                                   is inserted into the code processed by an
                                   invoking handler. The parameter is null if
                                   and only if the event argument equals to TAG.
       @return The old or new current TOMNode in case of event is TAG, or null
               in all other cases (i.e. the returned value is simply ignored, then)
     */
    public TOMNode handleTagEnd(CallContext context, TCEvent event, TOMNode current_node, String tag_name, Vector<String> arguments, TCHandler ascii_source_handler, StringBuffer out) throws InvalidTemplateSyntax;

    /**
       Optionally returns a list of tags that can be handled by this
       Handler. When by registering a handler in a templates the registered
       tag name is "*" (wildcard), then the handler is registered for all tags
       returned by this method.

       @return A list of handled tags, or null.
     */
    public Vector<String> getHandledTags(CallContext context);

    /**
       Returns true, if and only if this handler is capable of handling
       arbitrarily named tags.

       @return true, if all tagnames can be handled.
     */
    public boolean        getAcceptsAllTags(CallContext context);
}
