package com.sphenon.engines.generator.classes;

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
import com.sphenon.basics.encoding.*;
import com.sphenon.basics.expression.*;
import com.sphenon.basics.data.*;
import com.sphenon.basics.metadata.*;
import com.sphenon.basics.locating.*;
import com.sphenon.basics.graph.*;
import com.sphenon.basics.graph.tplinst.*;
import com.sphenon.basics.graph.factories.*;
import com.sphenon.basics.graph.files.factories.*;
import com.sphenon.basics.graph.classes.*;
import com.sphenon.basics.validation.returncodes.*;

import com.sphenon.basics.configuration.annotations.*;

import com.sphenon.engines.generator.*;
import com.sphenon.engines.generator.returncodes.*;

import java.util.Vector;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.io.IOException;
import java.io.InputStream;

import java.io.File;
import java.util.Date;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.ByteArrayInputStream;

public class Class_Template implements Template {

    // Configuration ------------------------------------------------------------------

    @Configuration public interface Config {
        String getTemplatePath(CallContext context);
        void   setTemplatePath(CallContext context, String template_path);

        String getTemplatePackagePath(CallContext context);
        void   setTemplatePackagePath(CallContext context, String template_package_path);
    }
    static public Config config = Configuration_Class_Template.get(RootContext.getInitialisationContext());

    // Construction -------------------------------------------------------------------

    public Class_Template(CallContext context) {
    }

    public Class_Template(CallContext context, String full_class_name) throws NoSuchTemplate {
        init(context, full_class_name, null, null, false);
    }

    public Class_Template(CallContext context, String full_class_name, boolean is_module) throws NoSuchTemplate {
        init(context, full_class_name, null, null, is_module);
    }

    public Class_Template(CallContext context, String full_class_name, String text_locator) throws NoSuchTemplate {
        init(context, full_class_name, text_locator, null, false);
    }

    public Class_Template(CallContext context, String full_class_name, String text_locator, boolean is_module) throws NoSuchTemplate {
        init(context, full_class_name, text_locator, null, is_module);
    }

    public Class_Template(CallContext context, String full_class_name, TreeLeaf tree_leaf) {
        try {
            init(context, full_class_name, null, tree_leaf, false);
        } catch (NoSuchTemplate nst) {
            CustomaryContext.create((Context)context).throwImpossibleState(context, nst, "That should not happen");
            throw (ExceptionImpossibleState) null; // compiler insists
        }
    }

    public Class_Template(CallContext context, String full_class_name, TreeLeaf tree_leaf, boolean is_module) {
        try {
            init(context, full_class_name, null, tree_leaf, is_module);
        } catch (NoSuchTemplate nst) {
            CustomaryContext.create((Context)context).throwImpossibleState(context, nst, "That should not happen");
            throw (ExceptionImpossibleState) null; // compiler insists
        }
    }

    protected void init(CallContext context, String full_class_name, String text_locator, TreeLeaf tree_leaf, boolean is_module) throws NoSuchTemplate {
        this.setFullClassName(context, full_class_name);
        this.is_module = is_module;

        if (text_locator != null) {
            try {
                TreeNode template_node = (Factory_TreeNode.construct(context, text_locator));
                if (template_node instanceof TreeLeaf) {
                    tree_leaf = (TreeLeaf) template_node;
                } else {
                    tree_leaf = createFolderDescriptionLeaf(context, template_node, null, null);
                }
            } catch (ValidationFailure vf) {
                NoSuchTemplate.createAndThrow(context, vf, "Template '%(locator)' does not exist", "locator", text_locator);
                throw (NoSuchTemplate) null; // compiler insists
            }
        }

        if (tree_leaf != null) {
            if (text_locator == null) {
                text_locator = tree_leaf.getLocation(context).getUniqueIdentifier(context);
            }
            this.tree_leaf = tree_leaf;

            if (full_class_name == null) {
                String sha_code = Encoding.recode_UTF8_SHA1(context, text_locator);
                this.setFullClassName(context, "com.sphenon.engines.generator.templates.treenodes." + Encoding.recode(context, text_locator.replaceFirst("^.*/", "").replaceFirst("\\.template$", ""), Encoding.UTF8, Encoding.VSA) + "_SHA1_" + sha_code);
            }
        } else {
            this.findSourceInPath(context);
        }

    }

    public Class_Template(CallContext context, String full_class_name, File source_file) throws NoSuchTemplate {
        this.setFullClassName(context, full_class_name);
        try {
            this.tree_leaf = (TreeLeaf) Factory_TreeNode_File.construct(context, source_file, NodeType.LEAF);
        } catch (ValidationFailure vf) {
            NoSuchTemplate.createAndThrow(context, vf, ""); // i.e. in case of message "" the message of vf should be shown
        }
        this.is_module = false;
    }

    // Attributes ---------------------------------------------------------------------

    protected boolean is_module;

    protected TreeLeaf tree_leaf;

    public Vector<String> getPackageChilds(CallContext context) {
        TreeNode parent = tree_leaf.tryGetParent(context);
        if (parent == null) { return null; }
        Vector<String> result = new Vector<String>();
        NodeFilter filter = new NodeFilterRegExp (context, null, ".*", "^.*\\.([A-Za-z0-9_]+-)?template$", null, null, null);
        for (TreeNode child : parent.getChilds(context, filter).getIterable_TreeNode_(context)) {
            result.add(child.getId(context).replaceFirst("\\.([A-Za-z0-9_]+-)?template$", ""));
        }
        return result;
    }

    public Data_MediaObject getSource (CallContext context) {
        return ((Data_MediaObject)(((NodeContent_Data) (tree_leaf.getContent(context))).getData(context)));
    }

    static protected RegularExpression template_type_re = new RegularExpression(".*\\.(?:([A-Za-z0-9_]+)-)?template(?:\\.module)?$");

    public String getTemplateTypeAlias(CallContext context) {
        String id = this.tree_leaf != null ? this.tree_leaf.getId(context) : null;
        String[] matches = template_type_re.tryGetMatches(context, id);
        if (matches != null) {
            return matches[0];
        }
        return null;
    }

    public long getLastModification(CallContext context) {
        return this.tree_leaf.getLastModification(context);
    }

    public Locator tryGetOrigin(CallContext context) {
        return this.tree_leaf.getLocation(context).getLocators(context).get(0);
    }

    protected String class_name;

    public String getClassName (CallContext context) {
        return this.class_name;
    }

    protected String package_name;

    public String getPackageName (CallContext context) {
        return this.package_name;
    }

    protected String full_class_name;

    public String getFullClassName (CallContext context) {
        return this.full_class_name;
    }

    public void setFullClassName (CallContext context, String full_class_name) {
        this.full_class_name = full_class_name;

        if (this.full_class_name != null) {
            int    pos           = full_class_name.lastIndexOf(".");
            this.package_name    = (pos == -1 ? "" : full_class_name.substring(0,pos));
            this.class_name      = (pos == -1 ? full_class_name : full_class_name.substring(pos+1));
        } else {
            this.package_name    = null;
            this.class_name      = null;
        }
    }

    // Internal -----------------------------------------------------------------------

    protected void findSourceInPath (CallContext context) throws NoSuchTemplate {
        String package_path = this.getPackageName(context);
        if (package_path == null || package_path.length() == 0) {
            package_path = config.getTemplatePackagePath(context);
        }
        String class_regexp = "^" + this.getClassName(context) + "\\.([A-Za-z0-9_]+-)?template" + (is_module ? "\\.module" : "") + "$";
        NodeFilter filter = new NodeFilterRegExp(context, class_regexp, null, class_regexp, null, null, null);
        String tp = config.getTemplatePath(context);
        for (String path_entry : tp.split(":")) {
            for (String package_path_entry : package_path.split(":")) {
                String ppe = package_path_entry.replace(".","/");
                String locator = Encoding.recode(context, path_entry, Encoding.URI, Encoding.UTF8);
                // WTF?
                locator = Encoding.recode(context, locator, Encoding.UTF8, Encoding.JAVA);
                locator += "/" + ppe;
                TreeNode tree_node = Factory_TreeNode.tryConstruct(context, locator);
                if (tree_node != null) {
                    if (tree_node instanceof TreeLeaf) {
                        // skip, this is not a folder
                    } else {
                        Vector_TreeNode_long_ childs = tree_node.getChilds(context, filter);
                        for (TreeNode child : childs.getIterable_TreeNode_(context)) {
                            if (child instanceof TreeLeaf) {
                                this.tree_leaf = (TreeLeaf) child;
                            } else {
                                this.tree_leaf = createFolderDescriptionLeaf(context, child, package_path_entry, child.getId(context));
                            }

                            this.package_name = package_path_entry;
                            this.full_class_name = this.package_name + "." + this.class_name;
                            return;
                        }
                    }
                }
            }
        }
        NoSuchTemplate.createAndThrow(context, "Template '%(template)' not found in path '%(path)' and package path '%(packagepath)'", "template", this.getFullClassName(context), "path", config.getTemplatePath(context), "packagepath", package_path);
        throw (NoSuchTemplate) null; // compiler insists
    }

    protected TreeLeaf createFolderDescriptionLeaf(CallContext context, TreeNode folder, String node_path, String node_name) {
        // spaeter sollte das hier auch template werden ! :-)
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        OutputStreamWriter osw = null;
        try {
            osw = new OutputStreamWriter(baos, "UTF-8");
            TreeNode typenode = folder.tryGetChild(context, ".TYPE");
            if (typenode != null && typenode instanceof TreeLeaf) {
                copyLeaf(context, (TreeLeaf) typenode, osw);
            } else {
                osw.write("G-2.0-files-1.0 -*- coding: utf-8; -*-\n");
            }
            osw.write("«\n");
            if (node_path != null && node_name != null) {
                node_path = Encoding.recode(context, node_path, Encoding.UTF8, Encoding.JAVA);
                node_name = Encoding.recode(context, node_name, Encoding.UTF8, Encoding.JAVA);
                osw.write("rootnode = Class_Template.findNodeInPath(context, \"" + node_path + "\", \"" + node_name + "\");\n");
            } else {
                String locator = folder.getLocation(context).getLocators(context).get(0).getPartialTextLocator(context); 
                locator = Encoding.recode(context, locator, Encoding.UTF8, Encoding.JAVA);
                osw.write("rootnode = Factory_TreeNode.tryConstruct(context, \"" + locator + "\");\n");
            }
            osw.write("rebuildNodes(context);\n");
            osw.write("rebuildPathes(context);\n");
            processNode(context, folder, folder, osw, false);
            osw.write("»\n");
            osw.close();
        } catch (UnsupportedEncodingException uee) {
            CustomaryContext.create((Context)context).throwInstallationError(context, uee, "runtime environment does not support UTF-8 encoding");
            throw (ExceptionInstallationError) null; // compiler insists
        } catch (IOException ioe) {
            CustomaryContext.create((Context)context).throwEnvironmentFailure(context, ioe, "Could not close temporary byte array stream");
            throw (ExceptionEnvironmentFailure) null; // compiler insists
        }
        return TreeLeaf_AnonymousByteArray.create(context, baos.toByteArray(), TypeManager.getMediaType(context, "template"), folder.getLocation(context));
    }

    static public TreeNode findNodeInPath(CallContext context, String node_path, String node_name) {
        String tp = config.getTemplatePath(context);
        String node_locator = node_path.replace(".","/") + "/" + node_name;
        for (String path_entry : tp.split(":")) {
            String locator = Encoding.recode(context, path_entry, Encoding.URI, Encoding.UTF8);
            // WTF?
            locator = Encoding.recode(context, locator, Encoding.UTF8, Encoding.JAVA);
            locator += "/" + node_locator;
            TreeNode tree_node = Factory_TreeNode.tryConstruct(context, locator);
            if (tree_node != null) {
                return tree_node;
            }
        }
        CustomaryContext.create((Context)context).throwConfigurationError(context, "During processing of folder based generator template '%(template)' the underlying resources could not be found again in path '%(path)' - this might be due to misconfiguration between precompiling environment and execution environment", "template", node_locator, "path", tp);
        throw (ExceptionConfigurationError) null; // compiler insists
    }

    protected String prepareId(CallContext context, String id, boolean is_leaf) {
        if (is_leaf) { id = id.replaceFirst("\\.template$",""); }
        id = Encoding.recode(context, id, Encoding.UTF8, Encoding.JAVA);
        return id.replaceAll("@([^@]+)@","\" + $1 + \"");
    }

    protected void copyLeaf(CallContext context, TreeLeaf leaf, OutputStreamWriter osw) throws IOException {
        InputStream is = ((Data_MediaObject)(((NodeContent_Data)(leaf.getContent(context))).getData(context))).getStream(context);
        InputStreamReader isr = new InputStreamReader(is, "UTF-8");
        BufferedReader br = new BufferedReader(isr);
        
        String line;
        while ((line = br.readLine()) != null) {
            osw.write(line + "\n");
        }
        
        br.close();
        isr.close();
        is.close();
    }

    protected void processNode(CallContext context, TreeNode node, TreeNode root, OutputStreamWriter osw, boolean linked) throws IOException {
        String relative_path = node.getLocation(context).tryGetTextLocatorValue(context, root.getLocation(context), "Path");

        String  node_id = node.getId(context);
        boolean is_leaf = (node instanceof TreeLeaf);
        boolean is_link = (is_leaf && node.getId(context).matches(".*\\.LINK(?:-[0-9]+)"));

        TreeNode linked_node = null;
        if (is_link) {
            node_id = node_id.replaceFirst("\\.LINK(?:-[0-9]+)$", "");
            Vector<String> lines = NodeUtilities.tryReadLeaf(context, (TreeLeaf) node);
            if (lines == null || lines.size() == 0 || lines.get(0).isEmpty()) {
                CustomaryContext.create((Context)context).throwConfigurationError(context, "LINK in generator template folder needs to contain at least one non-empty line at the beginning");
                throw (ExceptionConfigurationError) null; // compiler insists
            }
            String link = lines.get(0);
            linked_node = root.tryGetChild(context, link);
            if (linked_node == null) {
                CustomaryContext.create((Context)context).throwConfigurationError(context, "LINK '%(link)' in generator template folder needs to contain at least one non-empty line at the beginning", "link", link);
                throw (ExceptionConfigurationError) null; // compiler insists
            }
            osw.write("pushSourceStack(context);\n");
            osw.write("pushSourceId(context, \"" + Encoding.recode(context, link, Encoding.UTF8, Encoding.JAVA) + "\");\n");
        }

        TreeNode before;
        TreeNode after;

        before = node.tryGetParent(context).tryGetChild(context, node_id + ".BEFORE");
        if (before != null && before instanceof TreeLeaf) {
            copyLeaf(context, (TreeLeaf) before, osw);
        }

        boolean tp = false;
        if (    node != root
             && linked == false
             && (    is_leaf
                  || node_id.matches(".*\\.GROUP") == false
                )
           ) {
            tp = true;
            osw.write("pushTargetId(context, \"" + prepareId(context, node_id, is_leaf) + "\");\n");
        }

        if (is_link) {
            processNode(context, linked_node, root, osw, true);
        } else if (is_leaf) {
            osw.write("createFile(context, local_arguments);\n");
        } else {
            if (node_id.matches("CVS|\\.svn") == false) {
                before = node.tryGetChild(context, ".BEFORE");
                if (before != null && before instanceof TreeLeaf) {
                    copyLeaf(context, (TreeLeaf) before, osw);
                }
                osw.write("createFolder(context);\n");
                if (node.getChilds(context) != null) {
                    for (TreeNode child_node : node.getChilds(context).getIterable_TreeNode_(context)) {
                        if (    child_node.getId(context).matches(".*\\.(IGNORE)") == false
                             && (
                                     (child_node instanceof TreeLeaf) == false
                                  || (    child_node.getId(context).matches(".*\\.(BEFORE|AFTER)")
                                       || (    child_node.getId(context).matches(".*\\.(TYPE)")
                                            && node == root
                                          )
                                     ) == false
                                )
                           ) {
                            osw.write("pushSourceId(context, \"" + Encoding.recode(context, child_node.getId(context), Encoding.UTF8, Encoding.JAVA) + "\");\n");
                            processNode(context, child_node, root, osw, false);
                            osw.write("popSourceId(context);\n");
                        }

                    }
                }
                after = node.tryGetChild(context, ".AFTER");
                if (after != null && after instanceof TreeLeaf) {
                    copyLeaf(context, (TreeLeaf) after, osw);
                }
            }
        }

        if (tp) {
            osw.write("popTargetId(context);\n");
        }

        after = node.tryGetParent(context).tryGetChild(context, node_id + ".AFTER");
        if (after != null && after instanceof TreeLeaf) {
            copyLeaf(context, (TreeLeaf) after, osw);
        }

        if (is_link) {
            osw.write("popSourceId(context);\n");
            osw.write("popSourceStack(context);\n");
        }
    }

    // Operations ---------------------------------------------------------------------

    public BufferedReader getReader (CallContext context) {
        try {
            return new BufferedReader(new InputStreamReader(this.getSource(context).getStream(context), "UTF-8"));
        } catch (UnsupportedEncodingException uee) {
            CustomaryContext.create((Context)context).throwInstallationError(context, uee, "runtime environment does not support UTF-8 encoding");
            throw (ExceptionInstallationError) null; // compiler insists
        }
    }
}
