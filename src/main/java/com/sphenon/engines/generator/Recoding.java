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
import com.sphenon.basics.locating.*;

import com.sphenon.engines.generator.*;

import java.util.Vector;
import java.io.BufferedWriter;
import java.io.IOException;

public class Recoding {

    public Recoding(CallContext context, Vector<String[]> sequence) {
        this.is_cloned = false;
        this.sequence = sequence;
    }

    public Recoding(CallContext context) {
        this.is_cloned = false;
        this.sequence = new Vector<String[]>();
        {   String[] entry = new String[2];
            entry[0] = "UTF8";
            entry[1] = null;
            this.sequence.add(entry);
        }
        {   String[] entry = new String[2];
            entry[0] = "UTF8";
            entry[1] = null;
            this.sequence.add(entry);
        }
    }

    protected Vector<String[]> sequence;

    public Vector<String[]> getSequence (CallContext context) {
        return this.sequence;
    }

    public void setSequence (CallContext context, Vector<String[]> sequence) {
        this.sequence = sequence;
    }

    public String[] getSourceEncoding(CallContext context) {
        int size = (sequence == null ? 0 : sequence.size());
        String[] senc = (size > 0 ? this.sequence.get(0) : null);
        return (    senc != null
                 && senc.length != 0
                 && senc[0] != null
                 && senc[0].length() != 0
               ) ? senc : null;
    }

    public String[] getTargetEncoding(CallContext context) {
        int size = (sequence == null ? 0 : sequence.size());
        String[] tenc = (size > 1 ? this.sequence.get(size-1) : null);
        return (    tenc != null
                 && tenc.length != 0
                 && tenc[0] != null
                 && tenc[0].length() != 0
               ) ? tenc : null;
    }

    public boolean isComplete(CallContext context) {
        int size = (sequence == null ? 0 : sequence.size());
        String[] senc = (size > 1 ? sequence.get(0) : null);
        String[] tenc = (size > 2 ? sequence.get(size-1) : null);
        return (    size > 2
                 && senc[0] != null && senc[0].length() != 0
                 && tenc[0] != null && tenc[0].length() != 0
               );
    }

    public void augment(CallContext context, Recoding recoding) {
        String[] my_senc = this.getSourceEncoding(context);
        String[] my_tenc = this.getTargetEncoding(context);
        String[] other_senc = recoding.getSourceEncoding(context);
        String[] other_tenc = recoding.getTargetEncoding(context);
        if (my_senc == null && other_senc != null) {
            cloneSequence(context);
            this.sequence.set(0, other_senc); 
        }
        if (my_tenc == null && other_tenc != null) {
            cloneSequence(context);
            this.sequence.set(this.sequence.size()-1, other_tenc); 
        }
    }

    static protected String[] default_encoding = { "UTF8", null };

    public void augmentDefault(CallContext context) {
        String[] my_senc = this.getSourceEncoding(context);
        String[] my_tenc = this.getTargetEncoding(context);
        if (my_senc == null) {
            cloneSequence(context);
            this.sequence.set(0, default_encoding); 
        }
        if (my_tenc == null) {
            cloneSequence(context);
            this.sequence.set(this.sequence.size()-1, default_encoding); 
        }
    }

    protected boolean is_cloned = false;

    protected void cloneSequence(CallContext context) {
        if ( ! is_cloned) {
            is_cloned = true;
            Vector<String[]> new_sequence = new Vector<String[]>();
            for (String[] entry : this.sequence) {
                String[] new_entry = new String[2];
                new_entry[0] = entry[0];
                new_entry[1] = entry[1];
                new_sequence.add(new_entry);
            }
            if (new_sequence.size() == 0) {
                new_sequence.add(new String[2]);
            }
            if (new_sequence.size() == 1) {
                new_sequence.add(new String[2]);
            }
            this.sequence = new_sequence;
        }
    }

    public Recoding clone(CallContext context) {
        return new Recoding(context, this.getSequence(context));
    }

    public void writeEncodingPrefix(CallContext context, BufferedWriter current_writer) throws IOException {
        Vector<String[]> sequence = this.getSequence(context);
        if (sequence == null) { return; }
        int seqsize = sequence.size();
        if (seqsize == 0) { return; }

        for (int i=seqsize-2; i>=0; i--) {
            String[] senc = sequence.get(i);
            String[] tenc = sequence.get(i+1);
            if (senc == null || tenc == null) { continue; }
            if (senc.length != 2 || senc[0] == null || senc[0].length() == 0) {
                CustomaryContext.create((Context)context).throwAssertionProvedFalse(context, "In recoding, a sequence unexpectedly contains an invalid entry (size != 2 at encoding step #%(index))", "index", i);
                throw (ExceptionAssertionProvedFalse) null; // compiler insists
            }
            if (tenc.length != 2 || tenc[0] == null || tenc[0].length() == 0) {
                CustomaryContext.create((Context)context).throwAssertionProvedFalse(context, "In recoding, a sequence unexpectedly contains an invalid entry ( != 2 at encoding step #%(index))", "index", i+1);
                throw (ExceptionAssertionProvedFalse) null; // compiler insists
            }
            if (senc[0].equals(tenc[0]) == false) {
                current_writer.append("Encoding.recode(context, ");
            }
        }
    }

    public void writeEncodingPostfix(CallContext context, BufferedWriter current_writer) throws IOException {
        Vector<String[]> sequence = this.getSequence(context);
        if (sequence == null) { return; }
        int seqsize = sequence.size();
        if (seqsize == 0) { return; }

        for (int i=0; i<seqsize-1; i++) {
            String[] senc = sequence.get(i);
            String[] tenc = sequence.get(i+1);
            if (senc == null || tenc == null) { continue; }
            if (senc[0].equals(tenc[0]) == false) {
                current_writer.append(", Encoding." + senc[0] + ", Encoding." + tenc[0] + (tenc[1] != null && tenc[1].length() != 0 ? (", " + tenc[1]) : "") + ")");
            }
        }
    }

    public void writeEncodingStepsInitializer(CallContext context, BufferedWriter current_writer, String indent) throws IOException {
        Vector<String[]> sequence = this.getSequence(context);
        if (sequence == null) { return; }
        int seqsize = sequence.size();
        if (seqsize == 0) { return; }

        current_writer.append(indent + "    {\n");

        for (int i=0; i<seqsize; i++) {
            String[] enc = sequence.get(i);
            if (enc == null || enc.length != 2 || enc[0] == null || enc[0].length() == 0) {
                current_writer.append(indent + "        new EncodingStep(context, (Encoding) null)" + (i == seqsize-1 ? "\n" : ",\n"));
            } else {
                current_writer.append(indent + "        new EncodingStep(context, Encoding." + enc[0]);

                String options = (enc[1] != null && enc[1].length() != 0 ? enc[1] : null);
                if (options != null) {
                    if (options.indexOf('(') != -1) {
                        current_writer.append(") { public Object[] getOptions (CallContext context) { return buildArray(" + options + "); } }");
                    } else {
                        current_writer.append(", " + options + ")");
                    }
                } else {
                    current_writer.append(")");
                }

                current_writer.append((i == seqsize-1 ? "\n" : ",\n"));
            }
        }

        for (int i=seqsize; i<2; i++) {
            current_writer.append(indent + "        new EncodingStep(context, (Encoding) null)" + (i == 1 ? "\n" : ",\n"));
        }

        current_writer.append(indent + "    }");
    }
}
