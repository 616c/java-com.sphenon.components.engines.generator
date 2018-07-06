/* Generated By:JavaCC: Do not edit this line. LanguageDispatcher.java */

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
package com.sphenon.engines.generator.tchandler.classes;

import com.sphenon.basics.context.*;
import com.sphenon.engines.generator.*;
import com.sphenon.engines.generator.tom.*;
import com.sphenon.engines.generator.tchandler.*;
import com.sphenon.engines.generator.returncodes.*;

import java.io.Reader;
import java.io.Writer;
import java.io.IOException;

public class LanguageDispatcher implements LanguageDispatcherConstants {

  protected Writer                out;
  protected TCHLanguageDispatcher tch;

  public LanguageDispatcher (CallContext context, Reader in, TCHLanguageDispatcher tch) {
      this(in);
      this.out = out;
      this.tch = tch;
  }

  public String getPosition(CallContext context) {
      return   "[line "    + jj_input_stream.getBeginLine()
                           + (jj_input_stream.getBeginLine() != jj_input_stream.getEndLine() ? ("-" + jj_input_stream.getEndLine()) : "")
             + ", column " + jj_input_stream.getBeginColumn()
                           + (jj_input_stream.getBeginColumn() != jj_input_stream.getEndColumn() ? ("-" + jj_input_stream.getEndColumn()) : "")
             + "]";
  }

  final public void Any(CallContext context, TCEvent tag_context_event, TOMNode current_node, StringBuffer value) throws ParseException, InvalidTemplateSyntax {
                                                                                                                                    Token token;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case PERPER:
      token = jj_consume_token(PERPER);
                           value.append("%%");
      break;
    case PERCLO:
      token = jj_consume_token(PERCLO);
                           value.append("%>");
      break;
    case OPEPER:
      token = jj_consume_token(OPEPER);
                           value.append("<%");
      break;
    case ATCLO:
      token = jj_consume_token(ATCLO);
                           value.append("@>");
      break;
    case OPEAT:
      token = jj_consume_token(OPEAT);
                           value.append("<@");
      break;
    case BACKSL:
      token = jj_consume_token(BACKSL);
                           value.append("\\");
      break;
    case RETURN:
      token = jj_consume_token(RETURN);
                           value.append("\n");
      break;
    case ANY:
      token = jj_consume_token(ANY);
                           value.append(token.image);
      break;
    case PPTAGB1:
    case PPTAGB2:
      PPTag(context, tag_context_event, current_node, value);
      break;
    default:
      jj_la1[0] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
  }

  final public void NewLine(StringBuffer value) throws ParseException {
                                     Token token;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case LN:
      token = jj_consume_token(LN);
                        value.append(token.image);
      break;
    case LNCONT:
      jj_consume_token(LNCONT);
      break;
    default:
      jj_la1[1] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
  }

  final public void BeginningOfLine(CallContext context, TCEvent tag_context_event, TOMNode current_node, StringBuffer value) throws ParseException, InvalidTemplateSyntax {
                                                                                                                                                Token token;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case LNINDT:
      token = jj_consume_token(LNINDT);
      break;
    case WS:
      token = jj_consume_token(WS);
                        value.append(token.image);
      break;
    case OPEPER:
    case PERCLO:
    case PPTAGB1:
    case PPTAGB2:
    case OPEAT:
    case ATCLO:
    case BACKSL:
    case RETURN:
    case PERPER:
    case ANY:
      Any(context, tag_context_event, current_node, value);
      break;
    default:
      jj_la1[2] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
  }

  final public void RestOfLine(CallContext context, TCEvent tag_context_event, TOMNode current_node, StringBuffer value) throws ParseException, InvalidTemplateSyntax {
                                                                                                                                           Token token;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case LNINDT:
      token = jj_consume_token(LNINDT);
                        value.append(token.image);
      break;
    case WS:
      token = jj_consume_token(WS);
                        value.append(token.image);
      break;
    case OPEPER:
    case PERCLO:
    case PPTAGB1:
    case PPTAGB2:
    case OPEAT:
    case ATCLO:
    case BACKSL:
    case RETURN:
    case PERPER:
    case ANY:
      Any(context, tag_context_event, current_node, value);
      break;
    default:
      jj_la1[3] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
  }

  final public void NLLine(CallContext context, TCEvent tag_context_event, TOMNode current_node, StringBuffer value) throws ParseException, InvalidTemplateSyntax {
                                                                                                                                       Token token;
    NewLine(value);
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case OPEPER:
    case PERCLO:
    case PPTAGB1:
    case PPTAGB2:
    case OPEAT:
    case ATCLO:
    case LNINDT:
    case WS:
    case BACKSL:
    case RETURN:
    case PERPER:
    case ANY:
      BeginningOfLine(context, tag_context_event, current_node, value);
      label_1:
      while (true) {
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case OPEPER:
        case PERCLO:
        case PPTAGB1:
        case PPTAGB2:
        case OPEAT:
        case ATCLO:
        case LNINDT:
        case WS:
        case BACKSL:
        case RETURN:
        case PERPER:
        case ANY:
          ;
          break;
        default:
          jj_la1[4] = jj_gen;
          break label_1;
        }
        RestOfLine(context, tag_context_event, current_node, value);
      }
      break;
    default:
      jj_la1[5] = jj_gen;
      ;
    }
  }

  final public StringBuffer FirstContent(CallContext context, TCEvent tag_context_event, TOMNode current_node) throws ParseException, InvalidTemplateSyntax {
                                                                                                                                 Token token; StringBuffer value;
    value = new StringBuffer();
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case OPEPER:
    case PERCLO:
    case PPTAGB1:
    case PPTAGB2:
    case OPEAT:
    case ATCLO:
    case LNINDT:
    case WS:
    case BACKSL:
    case RETURN:
    case PERPER:
    case ANY:
      BeginningOfLine(context, tag_context_event, current_node, value);
      label_2:
      while (true) {
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case OPEPER:
        case PERCLO:
        case PPTAGB1:
        case PPTAGB2:
        case OPEAT:
        case ATCLO:
        case LNINDT:
        case WS:
        case BACKSL:
        case RETURN:
        case PERPER:
        case ANY:
          ;
          break;
        default:
          jj_la1[6] = jj_gen;
          break label_2;
        }
        RestOfLine(context, tag_context_event, current_node, value);
      }
      label_3:
      while (true) {
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case LNCONT:
        case LN:
          ;
          break;
        default:
          jj_la1[7] = jj_gen;
          break label_3;
        }
        NLLine(context, tag_context_event, current_node, value);
      }
      break;
    case LNCONT:
    case LN:
      label_4:
      while (true) {
        NLLine(context, tag_context_event, current_node, value);
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case LNCONT:
        case LN:
          ;
          break;
        default:
          jj_la1[8] = jj_gen;
          break label_4;
        }
      }
      break;
    default:
      jj_la1[9] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    {if (true) return value;}
    throw new Error("Missing return statement in function");
  }

  final public StringBuffer Content(CallContext context, TCEvent tag_context_event, TOMNode current_node, StringBuffer value) throws ParseException, InvalidTemplateSyntax {
                                                                                                                                                Token token;
    if (value == null) { value = new StringBuffer(); }
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case OPEPER:
    case PERCLO:
    case PPTAGB1:
    case PPTAGB2:
    case OPEAT:
    case ATCLO:
    case LNINDT:
    case WS:
    case BACKSL:
    case RETURN:
    case PERPER:
    case ANY:
      label_5:
      while (true) {
        RestOfLine(context, tag_context_event, current_node, value);
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case OPEPER:
        case PERCLO:
        case PPTAGB1:
        case PPTAGB2:
        case OPEAT:
        case ATCLO:
        case LNINDT:
        case WS:
        case BACKSL:
        case RETURN:
        case PERPER:
        case ANY:
          ;
          break;
        default:
          jj_la1[10] = jj_gen;
          break label_5;
        }
      }
      label_6:
      while (true) {
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case LNCONT:
        case LN:
          ;
          break;
        default:
          jj_la1[11] = jj_gen;
          break label_6;
        }
        NLLine(context, tag_context_event, current_node, value);
      }
      break;
    case LNCONT:
    case LN:
      label_7:
      while (true) {
        NLLine(context, tag_context_event, current_node, value);
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case LNCONT:
        case LN:
          ;
          break;
        default:
          jj_la1[12] = jj_gen;
          break label_7;
        }
      }
      break;
    default:
      jj_la1[13] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    {if (true) return value;}
    throw new Error("Missing return statement in function");
  }

  final public TOMNode FirstText(CallContext context, TOMNode current_node) throws ParseException, InvalidTemplateSyntax {
                                                                                              Token token; StringBuffer value=null;
    value = FirstContent(context, TCEvent.TAG, current_node);
    {if (true) return tch.handleText(context, current_node, value.toString());}
    throw new Error("Missing return statement in function");
  }

  final public TOMNode Text(CallContext context, TOMNode current_node) throws ParseException, InvalidTemplateSyntax {
                                                                                         Token token; StringBuffer value=null;
    value = Content(context, TCEvent.TAG, current_node, null);
    {if (true) return tch.handleText(context, current_node, value.toString());}
    throw new Error("Missing return statement in function");
  }

  final public StringBuffer PossiblyNestedComment(CallContext context, TOMNode current_node, StringBuffer value) throws ParseException, InvalidTemplateSyntax {
                                                                                                                                   Token token;
    if (value == null) { value = new StringBuffer(); }
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case TCMTB:
      jj_consume_token(TCMTB);
      break;
    case TCMTB2:
      jj_consume_token(TCMTB2);
      break;
    default:
      jj_la1[14] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case OPEPER:
    case PERCLO:
    case PPTAGB1:
    case PPTAGB2:
    case OPEAT:
    case ATCLO:
    case LNCONT:
    case LNINDT:
    case WS:
    case BACKSL:
    case RETURN:
    case LN:
    case PERPER:
    case ANY:
      Content(context, null, current_node, value);
      break;
    default:
      jj_la1[15] = jj_gen;
      ;
    }
    label_8:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case TCMTB:
      case CODCB:
      case CODIB:
      case OUTCB:
      case DIRCB:
      case CODEB:
      case TCMTB2:
      case CODEE:
      case OUTCE:
      case DIRCE:
        ;
        break;
      default:
        jj_la1[16] = jj_gen;
        break label_8;
      }
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case TCMTB:
      case TCMTB2:
        PossiblyNestedComment(context, current_node, value);
        break;
      case CODCB:
        token = jj_consume_token(CODCB);
                          value.append(token.image);
        break;
      case CODIB:
        token = jj_consume_token(CODIB);
                          value.append(token.image);
        break;
      case OUTCB:
        token = jj_consume_token(OUTCB);
                          value.append(token.image);
        break;
      case OUTCE:
        token = jj_consume_token(OUTCE);
                          value.append(token.image);
        break;
      case CODEB:
        token = jj_consume_token(CODEB);
                          value.append(token.image);
        break;
      case CODEE:
        token = jj_consume_token(CODEE);
                          value.append(token.image);
        break;
      case DIRCB:
        token = jj_consume_token(DIRCB);
                          value.append(token.image);
        break;
      case DIRCE:
        token = jj_consume_token(DIRCE);
                          value.append(token.image);
        break;
      default:
        jj_la1[17] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case OPEPER:
      case PERCLO:
      case PPTAGB1:
      case PPTAGB2:
      case OPEAT:
      case ATCLO:
      case LNCONT:
      case LNINDT:
      case WS:
      case BACKSL:
      case RETURN:
      case LN:
      case PERPER:
      case ANY:
        Content(context, null, current_node, value);
        break;
      default:
        jj_la1[18] = jj_gen;
        ;
      }
    }
    jj_consume_token(TCMTE);
    {if (true) return value;}
    throw new Error("Missing return statement in function");
  }

  final public TOMNode TemplateComment(CallContext context, TOMNode current_node) throws ParseException, InvalidTemplateSyntax {
                                                                                                     Token token; StringBuffer value=null;
    value = PossiblyNestedComment(context, current_node, null);
    {if (true) return tch.handleTemplateComment(context, current_node, value.toString());}
    throw new Error("Missing return statement in function");
  }

  final public TOMNode JavaCodeGeneration(CallContext context, TOMNode current_node) throws ParseException, InvalidTemplateSyntax {
                                                                                                        Token token; StringBuffer value=null;
    jj_consume_token(CODEB);
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case OPEPER:
    case PERCLO:
    case PPTAGB1:
    case PPTAGB2:
    case OPEAT:
    case ATCLO:
    case LNCONT:
    case LNINDT:
    case WS:
    case BACKSL:
    case RETURN:
    case LN:
    case PERPER:
    case ANY:
      value = Content(context, TCEvent.JAVA_CODE_TAG, current_node, null);
      break;
    default:
      jj_la1[19] = jj_gen;
      ;
    }
    jj_consume_token(CODEE);
    {if (true) return tch.handleJavaCodeGeneration(context, current_node, value == null ? "" : value.toString());}
    throw new Error("Missing return statement in function");
  }

  final public TOMNode JavaCodeClass(CallContext context, TOMNode current_node) throws ParseException, InvalidTemplateSyntax {
                                                                                                   Token token; StringBuffer value=null;
    jj_consume_token(CODCB);
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case OPEPER:
    case PERCLO:
    case PPTAGB1:
    case PPTAGB2:
    case OPEAT:
    case ATCLO:
    case LNCONT:
    case LNINDT:
    case WS:
    case BACKSL:
    case RETURN:
    case LN:
    case PERPER:
    case ANY:
      value = Content(context, TCEvent.JAVA_CODE_TAG, current_node, null);
      break;
    default:
      jj_la1[20] = jj_gen;
      ;
    }
    jj_consume_token(CODEE);
    {if (true) return tch.handleJavaCodeClass(context, current_node, value == null ? "" : value.toString());}
    throw new Error("Missing return statement in function");
  }

  final public TOMNode JavaCodeImport(CallContext context, TOMNode current_node) throws ParseException, InvalidTemplateSyntax {
                                                                                                    Token token; StringBuffer value=null;
    jj_consume_token(CODIB);
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case OPEPER:
    case PERCLO:
    case PPTAGB1:
    case PPTAGB2:
    case OPEAT:
    case ATCLO:
    case LNCONT:
    case LNINDT:
    case WS:
    case BACKSL:
    case RETURN:
    case LN:
    case PERPER:
    case ANY:
      value = Content(context, TCEvent.JAVA_CODE_TAG, current_node, null);
      break;
    default:
      jj_la1[21] = jj_gen;
      ;
    }
    jj_consume_token(CODEE);
    {if (true) return tch.handleJavaCodeImport(context, current_node, value == null ? "" : value.toString());}
    throw new Error("Missing return statement in function");
  }

  final public TOMNode OutputCode(CallContext context, TOMNode current_node) throws ParseException, InvalidTemplateSyntax {
                                                                                                Token token; StringBuffer value=null;
    jj_consume_token(OUTCB);
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case OPEPER:
    case PERCLO:
    case PPTAGB1:
    case PPTAGB2:
    case OPEAT:
    case ATCLO:
    case LNCONT:
    case LNINDT:
    case WS:
    case BACKSL:
    case RETURN:
    case LN:
    case PERPER:
    case ANY:
      value = Content(context, TCEvent.JAVA_EXPRESSION_TAG, current_node, null);
      break;
    default:
      jj_la1[22] = jj_gen;
      ;
    }
    jj_consume_token(OUTCE);
    {if (true) return tch.handleJavaExpression(context, current_node, value == null ? "" : value.toString());}
    throw new Error("Missing return statement in function");
  }

  final public TOMNode DirectiveCode(CallContext context, TOMNode current_node) throws ParseException, InvalidTemplateSyntax {
                                                                                                   Token token; StringBuffer value=null;
    jj_consume_token(DIRCB);
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case OPEPER:
    case PERCLO:
    case PPTAGB1:
    case PPTAGB2:
    case OPEAT:
    case ATCLO:
    case LNCONT:
    case LNINDT:
    case WS:
    case BACKSL:
    case RETURN:
    case LN:
    case PERPER:
    case ANY:
      value = Content(context, TCEvent.TEMPLATE_CODE_TAG, current_node, null);
      break;
    default:
      jj_la1[23] = jj_gen;
      ;
    }
    jj_consume_token(DIRCE);
    {if (true) return tch.handleTemplateCode(context, current_node, value == null ? "" : value.toString());}
    throw new Error("Missing return statement in function");
  }

  final public TOMNode Tag(CallContext context, TCEvent tag_context_event, TOMNode current_node) throws ParseException, InvalidTemplateSyntax {
                                                                                                                    Token token; StringBuffer value=null;
    jj_consume_token(TAGB);
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case OPEPER:
    case PERCLO:
    case PPTAGB1:
    case PPTAGB2:
    case OPEAT:
    case ATCLO:
    case LNCONT:
    case LNINDT:
    case WS:
    case BACKSL:
    case RETURN:
    case LN:
    case PERPER:
    case ANY:
      value = Content(context, TCEvent.TAG_TAG, current_node, null);
      break;
    default:
      jj_la1[24] = jj_gen;
      ;
    }
    jj_consume_token(TAGE);
    {if (true) return tch.handleTag(context, tag_context_event, current_node, value == null ? "" : value.toString());}
    throw new Error("Missing return statement in function");
  }

  final public void PPTag(CallContext context, TCEvent tag_context_event, TOMNode current_node, StringBuffer value) throws ParseException, InvalidTemplateSyntax {
                                                                                                                                       Token token; StringBuffer tag_value=null;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case PPTAGB1:
      jj_consume_token(PPTAGB1);
      break;
    case PPTAGB2:
      jj_consume_token(PPTAGB2);
      break;
    default:
      jj_la1[25] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case OPEPER:
    case PERCLO:
    case PPTAGB1:
    case PPTAGB2:
    case OPEAT:
    case ATCLO:
    case LNCONT:
    case LNINDT:
    case WS:
    case BACKSL:
    case RETURN:
    case LN:
    case PERPER:
    case ANY:
      tag_value = Content(context, TCEvent.TAG_TAG, current_node, null);
      break;
    default:
      jj_la1[26] = jj_gen;
      ;
    }
    jj_consume_token(TAGE);
    tch.handlePPTag(context, tag_context_event, current_node, value, tag_value == null ? "" : tag_value.toString());
  }

  final public TOMNode ASCII(CallContext context, TOMNode current_node) throws ParseException, InvalidTemplateSyntax {
                                                                                           Token token;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case OPEPER:
    case PERCLO:
    case PPTAGB1:
    case PPTAGB2:
    case OPEAT:
    case ATCLO:
    case LNCONT:
    case LNINDT:
    case WS:
    case BACKSL:
    case RETURN:
    case LN:
    case PERPER:
    case ANY:
      current_node = FirstText(context, current_node);
      break;
    default:
      jj_la1[27] = jj_gen;
      ;
    }
    label_9:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case TCMTB:
      case CODCB:
      case CODIB:
      case OUTCB:
      case DIRCB:
      case CODEB:
      case TAGB:
      case TCMTB2:
        ;
        break;
      default:
        jj_la1[28] = jj_gen;
        break label_9;
      }
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case TCMTB:
      case TCMTB2:
        current_node = TemplateComment(context, current_node);
        break;
      case CODEB:
        current_node = JavaCodeGeneration(context, current_node);
        break;
      case CODCB:
        current_node = JavaCodeClass(context, current_node);
        break;
      case CODIB:
        current_node = JavaCodeImport(context, current_node);
        break;
      case OUTCB:
        current_node = OutputCode(context, current_node);
        break;
      case DIRCB:
        current_node = DirectiveCode(context, current_node);
        break;
      case TAGB:
        current_node = Tag(context, TCEvent.TAG, current_node);
        break;
      default:
        jj_la1[29] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case OPEPER:
      case PERCLO:
      case PPTAGB1:
      case PPTAGB2:
      case OPEAT:
      case ATCLO:
      case LNCONT:
      case LNINDT:
      case WS:
      case BACKSL:
      case RETURN:
      case LN:
      case PERPER:
      case ANY:
        current_node = Text(context, current_node);
        break;
      default:
        jj_la1[30] = jj_gen;
        ;
      }
    }
    jj_consume_token(0);
    {if (true) return current_node;}
    throw new Error("Missing return statement in function");
  }

  public LanguageDispatcherTokenManager token_source;
  SimpleCharStream jj_input_stream;
  public Token token, jj_nt;
  private int jj_ntk;
  private int jj_gen;
  final private int[] jj_la1 = new int[31];
  static private int[] jj_la1_0;
  static {
      jj_la1_0();
   }
   private static void jj_la1_0() {
      jj_la1_0 = new int[] {0xd8ec080,0x2100000,0xdeec080,0xdeec080,0xdeec080,0xdeec080,0xdeec080,0x2100000,0x2100000,0xffec080,0xdeec080,0x2100000,0x2100000,0xffec080,0x202,0xffec080,0x3a7e,0x3a7e,0xffec080,0xffec080,0xffec080,0xffec080,0xffec080,0xffec080,0xffec080,0x28000,0xffec080,0xffec080,0x37e,0x37e,0xffec080,};
   }

  public LanguageDispatcher(java.io.InputStream stream) {
     this(stream, null);
  }
  public LanguageDispatcher(java.io.InputStream stream, String encoding) {
    try { jj_input_stream = new SimpleCharStream(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
    token_source = new LanguageDispatcherTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 31; i++) jj_la1[i] = -1;
  }

  public void ReInit(java.io.InputStream stream) {
     ReInit(stream, null);
  }
  public void ReInit(java.io.InputStream stream, String encoding) {
    try { jj_input_stream.ReInit(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 31; i++) jj_la1[i] = -1;
  }

  public LanguageDispatcher(java.io.Reader stream) {
    jj_input_stream = new SimpleCharStream(stream, 1, 1);
    token_source = new LanguageDispatcherTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 31; i++) jj_la1[i] = -1;
  }

  public void ReInit(java.io.Reader stream) {
    jj_input_stream.ReInit(stream, 1, 1);
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 31; i++) jj_la1[i] = -1;
  }

  public LanguageDispatcher(LanguageDispatcherTokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 31; i++) jj_la1[i] = -1;
  }

  public void ReInit(LanguageDispatcherTokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 31; i++) jj_la1[i] = -1;
  }

  final private Token jj_consume_token(int kind) throws ParseException {
    Token oldToken;
    if ((oldToken = token).next != null) token = token.next;
    else token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    if (token.kind == kind) {
      jj_gen++;
      return token;
    }
    token = oldToken;
    jj_kind = kind;
    throw generateParseException();
  }

  final public Token getNextToken() {
    if (token.next != null) token = token.next;
    else token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    jj_gen++;
    return token;
  }

  final public Token getToken(int index) {
    Token t = token;
    for (int i = 0; i < index; i++) {
      if (t.next != null) t = t.next;
      else t = t.next = token_source.getNextToken();
    }
    return t;
  }

  final private int jj_ntk() {
    if ((jj_nt=token.next) == null)
      return (jj_ntk = (token.next=token_source.getNextToken()).kind);
    else
      return (jj_ntk = jj_nt.kind);
  }

  private java.util.Vector jj_expentries = new java.util.Vector();
  private int[] jj_expentry;
  private int jj_kind = -1;

  public ParseException generateParseException() {
    jj_expentries.removeAllElements();
    boolean[] la1tokens = new boolean[28];
    for (int i = 0; i < 28; i++) {
      la1tokens[i] = false;
    }
    if (jj_kind >= 0) {
      la1tokens[jj_kind] = true;
      jj_kind = -1;
    }
    for (int i = 0; i < 31; i++) {
      if (jj_la1[i] == jj_gen) {
        for (int j = 0; j < 32; j++) {
          if ((jj_la1_0[i] & (1<<j)) != 0) {
            la1tokens[j] = true;
          }
        }
      }
    }
    for (int i = 0; i < 28; i++) {
      if (la1tokens[i]) {
        jj_expentry = new int[1];
        jj_expentry[0] = i;
        jj_expentries.addElement(jj_expentry);
      }
    }
    int[][] exptokseq = new int[jj_expentries.size()][];
    for (int i = 0; i < jj_expentries.size(); i++) {
      exptokseq[i] = (int[])jj_expentries.elementAt(i);
    }
    return new ParseException(token, exptokseq, tokenImage);
  }

  final public void enable_tracing() {
  }

  final public void disable_tracing() {
  }

}
