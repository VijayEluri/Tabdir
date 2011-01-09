/*
 * Copyright 2010 Vladimir Rudev
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ru.crazycoder.plugins.tabdir.configuration;

import com.intellij.openapi.components.*;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.util.xmlb.XmlSerializer;
import org.jdom.Element;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: crazycoder
 * Date: Aug 15, 2010
 * Time: 6:48:00 PM
 */
@State(
        name = "TabdirConfiguration",
        storages = {@Storage(id = "dir", file = "$PROJECT_CONFIG_DIR$/other.xml", scheme = StorageScheme.DIRECTORY_BASED)})
public class Configuration
        implements PersistentStateComponent<Element> {

    private Logger log = Logger.getInstance(this.getClass().getCanonicalName());

    private static final String DEFAULT_TITLE_FORMAT = "[{0}] {1}";
    private static final String DEFAULT_DIR_SEPARATOR = "|";
    private static final String FOLDER_CONFIGURATIONS_NAME = "folderConfigurations";

    private boolean reduceDirNames;
    private int charsInName;
    private int maxDirsToShow;
    private FolderConfiguration.UseExtensionsEnum useExtensions;
    private String filesExtensions;
    private String dirSeparator;
    private String titleFormat;

    private Map<String, FolderConfiguration> folderConfigurations;

    private final PathMacroManager macroManager;

    public Configuration(Project project) {
        // todo move to configuration.xml
        // set default values to configuration
        reduceDirNames = true;
        charsInName = 5;
        maxDirsToShow = 3;
        useExtensions = FolderConfiguration.UseExtensionsEnum.DO_NOT_USE;
        filesExtensions = "java\ngroovy";
        dirSeparator = DEFAULT_DIR_SEPARATOR;
        titleFormat = DEFAULT_TITLE_FORMAT;

        folderConfigurations = new HashMap<String, FolderConfiguration>();
        // todo for test
        folderConfigurations.put("/home/ice/projects/untitled/src",
                new FolderConfiguration("", "", "", 10, 1, "", FolderConfiguration.UseExtensionsEnum.DO_NOT_USE));

        macroManager = PathMacroManager.getInstance(project);
    }

    @Override
    public Element getState() {
        Element configurationsElement = new Element(FOLDER_CONFIGURATIONS_NAME);
        for (Map.Entry<String, FolderConfiguration> entry : folderConfigurations.entrySet()) {
            Element element = new Element("Entry");
            element.setAttribute("folder", macroManager.collapsePath(entry.getKey()));
            Element folderConfig = XmlSerializer.serialize(entry.getValue());
            element.addContent(folderConfig);
            configurationsElement.addContent(element);
        }
        Element element = new Element("TabdirConfiguration");
        element.addContent(configurationsElement);
        return element;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void loadState(Element state) {
        Element configurationsElement = state.getChild(FOLDER_CONFIGURATIONS_NAME);
        if(configurationsElement == null) {
            log.debug("no config element");
            return;
        }
        Map<String, FolderConfiguration> folderConfigurations = new HashMap<String, FolderConfiguration>();
        List<Element> entrys = configurationsElement.getChildren("Entry");
        for (Element entry : entrys) {
            String key = macroManager.expandPath(entry.getAttributeValue("folder"));
            FolderConfiguration value = XmlSerializer.deserialize(entry.getChild("FolderConfiguration"), FolderConfiguration.class);
            folderConfigurations.put(key, value);
        }
        this.folderConfigurations = folderConfigurations;
    }

    public boolean isReduceDirNames() {
        return reduceDirNames;
    }

    public void setReduceDirNames(final boolean reduceDirNames) {
        this.reduceDirNames = reduceDirNames;
    }

    public String getFilesExtensions() {
        return filesExtensions;
    }

    public void setFilesExtensions(final String filesExtensions) {
        this.filesExtensions = filesExtensions;
    }

    public int getCharsInName() {
        return charsInName;
    }

    public void setCharsInName(final int charsInName) {
        this.charsInName = charsInName;
    }

    public int getMaxDirsToShow() {
        return maxDirsToShow;
    }

    public void setMaxDirsToShow(final int maxDirsToShow) {
        this.maxDirsToShow = maxDirsToShow;
    }

    public FolderConfiguration.UseExtensionsEnum getUseExtensions() {
        return useExtensions;
    }

    public void setUseExtensions(final FolderConfiguration.UseExtensionsEnum useExtensions) {
        this.useExtensions = useExtensions;
    }

    public String getDirSeparator() {
        return dirSeparator;
    }

    public void setDirSeparator(String dirSeparator) {
        this.dirSeparator = dirSeparator;
    }

    public String getTitleFormat() {
        return titleFormat;
    }

    public void setTitleFormat(String titleFormat) {
        this.titleFormat = titleFormat;
    }
}
