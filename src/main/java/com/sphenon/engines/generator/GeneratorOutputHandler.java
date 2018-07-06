package com.sphenon.engines.generator;

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
import com.sphenon.basics.encoding.*;

import com.sphenon.engines.generator.*;

import java.io.Writer;
import java.io.PrintWriter;

import java.util.Vector;

public interface GeneratorOutputHandler {
    /**
       Increases the usage count of this output handler. Each call to this
       method must be followed by a matching closeHandler invocation.
    */
    public void openHandler (CallContext context);

    /**
       Decreases the usage count and if it is zero, all writers will be closed.
    */
    public void closeHandler (CallContext context);

    /**
       Closes the current channel, independent if any usage counts.
    */
    public void closeCurrent (CallContext context);

    /**
       Gets the current writer and optionally creates it. If no channel
       redirection has taken place, the default writer (unnamed) is returned.
       The default writer is the one where all non classified generator output
       is written to. Many templates will only need this writer. 
    */
    public EncodingWriter getWriter (CallContext context);

    /**
       Gets a print writer wrapped around the current writer and maintains it,
       i.e. subsequent calls will return that same PrintWriter.
    */
    public PrintWriter getPrintWriter (CallContext context);

    /**
       Redirects the writer, so that each subsequent invocation of getWriter
       will return a writer which is associated with the channel named as
       given by the channel_name attribute. The primary default channel is
       refered to by an empty name (length zero, but not null).

       Note: The Writer instances itself are not changed, i.e. Writers
       obtained by previous calls will not be affected, only Writers retrieved
       subsequently.

       @param channel_name The channel to redirect to, may be empty to
                           refer to the primary default channel.
       @return The previous channel_name
    */
    public String redirectDefaultWriter (CallContext context, String channel_name);

    /**
       Like {@link #redirectDefaultWriter} with additional argument 'do_not_modify'.
    
       Note: implementations may not support this method. It is currently only
       supported by the OutputToFile handler.

       @param channel_name The channel to redirect to, may be empty to
                           refer to the primary default channel.
       @param do_not_modify If true, the target file or resource is not modified,
                            nevertheless, a check is made whether the current
                            output differs from the existing file content.
       @return The previous channel_name
    */
    public String redirectDefaultWriter (CallContext context, String channel_name, boolean do_not_modify);

    /**
       If output was redirected to a channel with the 'do_not_modify' flag set (see
       {@link #redirectDefaultWriter}), and if the implementation supports
       that flag, this method reports whether the written output so far
       differs from the previously stored output.

       @return Whether output differs up to the current point
    */
    public Boolean isContentDiffering(CallContext context);

    /**
       Return a vector of channel names. If the generator does not redirect
       it's output so that there is only the default channel in use, this
       method returns null.

       @return Vector of channels or null if only the default channel was
               used.
    */
    public Vector<String> getChannelNames(CallContext context);
}
