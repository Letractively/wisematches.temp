/*
 * Copyright (c) 2010, BillionGoods.
 */

package billiongoods.server.web.servlet.view;

import billiongoods.core.utils.StringComparators;
import freemarker.ext.dom.NodeModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.ui.Model;
import org.springframework.util.DefaultPropertiesPersister;
import org.springframework.util.PropertiesPersister;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.InputStreamReader;
import java.util.*;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public final class StaticContentGenerator {
    private String[] resourcesPaths;
    private String defaultEncoding = "UTF-8";
    private ResourceLoader resourceLoader = new DefaultResourceLoader();
    private PropertiesPersister propertiesPersister = new DefaultPropertiesPersister();

    private final Map<String, Map<Locale, NodeModel>> cachedFileNames = new HashMap<>();

    private static final Logger log = LoggerFactory.getLogger("billiongoods.web.view.StaticContentGenerator");

    public StaticContentGenerator() {
        this("classpath:/i18n/");
    }

    public StaticContentGenerator(String... resourcesPaths) {
        this.resourcesPaths = resourcesPaths;
    }

    /**
     * Loads resources for specified page and put loaded data into specified model. This handler
     * <p/>
     * The method adds two attributes to the model:
     * <ol>
     * <li><strong>infoId</strong> - name of the page. This id will be added to all ids and classes of the end HTML page.
     * <li><strong>infoModel</strong> - info model as a {@link NodeModel} object.
     * </ol>
     * <p/>
     * This method also adds {@code infoConverter} parameter that contains name of default
     * converter template. If required this parameter can be redefined or not used at all in
     * page where info is required.
     *
     * @param pageName the page name
     * @param model    the model to be modified
     * @param locale   the locale
     * @return {@code true} if data for specified page has been loaded and model has been updated;
     * {@code false} if there is no data for specified page or page unknown.
     */
    public boolean generatePage(String pageName, boolean reverseOrder, Model model, Locale locale) {
        return generatePage(pageName, pageName, reverseOrder, model, locale);
    }

    /**
     * Loads resources for specified page and put loaded data into specified model. This handler
     * <p/>
     * The method adds two attributes to the model:
     * <ol>
     * <li><strong>infoId</strong> - name of the page. This id will be added to all ids and classes of the end HTML page.
     * <li><strong>infoModel</strong> - info model as a {@link NodeModel} object.
     * </ol>
     * <p/>
     * This method also adds {@code infoConverter} parameter that contains name of default
     * converter template. If required this parameter can be redefined or not used at all in
     * page where info is required.
     *
     * @param pageName the page name
     * @param infoId   the id of the information page. This id will be added to all HTML elements into class name.
     * @param model    the model to be modified
     * @param locale   the locale
     * @return {@code true} if data for specified page has been loaded and model has been updated;
     * {@code false} if there is no data for specified page or page unknown.
     */
    public boolean generatePage(String pageName, String infoId, boolean reverseOrder, Model model, Locale locale) {
        final NodeModel nodeModel = getInfoHolder(pageName, locale);
        if (nodeModel == null) {
            return false;
        }

        model.addAttribute("staticContentId", infoId);
        model.addAttribute("staticContentModel", nodeModel);
        model.addAttribute("staticContentOrder", reverseOrder);
        return true;
    }

    protected NodeModel getInfoHolder(String name, Locale locale) {
        synchronized (cachedFileNames) {
            Map<Locale, NodeModel> holder = cachedFileNames.get(name);
            if (holder == null) {
                holder = new HashMap<>();
                cachedFileNames.put(name, holder);
            }

            NodeModel informationHolder = holder.get(locale);
            if (informationHolder == null) {
                informationHolder = loadInfoHolder(name, locale);
                if (informationHolder != null) {
                    holder.put(locale, informationHolder);
                }
            }
            return informationHolder;
        }
    }

    protected NodeModel loadInfoHolder(String name, Locale locale) {
        final List<String> filenames = new ArrayList<>();
        filenames.add(name);
        filenames.addAll(calculateFileNamesForLocale(name, locale));

        for (int j = filenames.size() - 1; j >= 0; j--) {
            final String filename = filenames.get(j);
            for (String path : resourcesPaths) {
                if (!path.endsWith(File.separator)) {
                    path = path + File.separator;
                }
                final Resource resource = resourceLoader.getResource(path + filename + ".properties");
                if (resource.exists()) {
                    return parseInfoHolder(resource);
                }
            }
        }
        return null;
    }

    protected NodeModel parseInfoHolder(Resource resource) {
        try {
            final Properties p = new Properties();
            propertiesPersister.load(p, new InputStreamReader(resource.getInputStream(), defaultEncoding));

            final DocumentBuilderFactory fact = DocumentBuilderFactory.newInstance();
            final DocumentBuilder bd = fact.newDocumentBuilder();
            final org.w3c.dom.Document document = bd.newDocument();

            final Set<String> keys = new TreeSet<>(StringComparators.getNaturalComparatorAscii());
            for (Object o : p.keySet()) {
                keys.add((String) o);
            }

            final Map<String, Element> cache = new HashMap<>();

            final Element root = document.createElement("item");
            cache.put(null, root);
            for (String key : keys) {
                final int i = key.lastIndexOf(".");
                final String cacheKey = i < 0 ? null : key.substring(0, i);
                final String propertyName = i < 0 ? key : key.substring(i + 1);

                Element informationHolder = cache.get(cacheKey);
                if (informationHolder == null) {
                    informationHolder = document.createElement("item");
                    informationHolder.setAttribute("id", cacheKey);
                    cache.put(cacheKey, informationHolder);

                    if (cacheKey != null) {
                        String name = cacheKey;
                        Element holder = informationHolder;
                        while (name != null) {
                            int k = name.lastIndexOf(".");
                            name = k < 0 ? null : name.substring(0, k);

                            Element h = cache.get(name);
                            if (h == null) {
                                h = document.createElement("item");
                                h.setAttribute("id", name);
                                cache.put(name, h);
                            } else {
                                name = null;
                            }

                            Node items = h.getElementsByTagName("items").item(0);
                            if (items == null) {
                                items = h.appendChild(document.createElement("items"));
                            }
                            items.appendChild(holder);
                            holder = h;
                        }
                    }
                }

                informationHolder.appendChild(document.createElement(propertyName)).appendChild(document.createTextNode(p.getProperty(key)));
            }

            NodeModel.simplify(root);
            NodeModel m = NodeModel.wrap(root);
            cache.clear();
            return m;
        } catch (Exception ex) {
            log.warn("Properties can't be loaded from resource {}", resource, ex);
            return null;
        }
    }

    public void setResourcesPaths(String... resourcesPaths) {
        this.resourcesPaths = resourcesPaths;
    }

    public void setDefaultEncoding(String defaultEncoding) {
        this.defaultEncoding = defaultEncoding;
    }

    /**
     * Set the ResourceLoader to use for loading bundle properties files.
     * <p>The default is a DefaultResourceLoader. Will get overridden by the
     * ApplicationContext if running in a context, as it implements the
     * ResourceLoaderAware interface. Can be manually overridden when
     * running outside of an ApplicationContext.
     *
     * @param resourceLoader the resource loaded. If null default resource loaded will be used
     * @see org.springframework.core.io.DefaultResourceLoader
     * @see org.springframework.context.ResourceLoaderAware
     */
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = (resourceLoader != null ? resourceLoader : new DefaultResourceLoader());
    }

    /**
     * Set the PropertiesPersister to use for parsing properties files.
     * <p>The default is a DefaultPropertiesPersister.
     *
     * @param propertiesPersister the properties persister. If null default properties persister will be used.
     * @see org.springframework.util.DefaultPropertiesPersister
     */
    public void setPropertiesPersister(PropertiesPersister propertiesPersister) {
        this.propertiesPersister = (propertiesPersister != null ? propertiesPersister : new DefaultPropertiesPersister());
    }

    protected List<String> calculateFileNamesForLocale(String baseName, Locale locale) {
        List<String> result = new ArrayList<>(3);
        String language = locale.getLanguage();
        String country = locale.getCountry();
        String variant = locale.getVariant();
        StringBuilder temp = new StringBuilder(baseName);

        temp.append('_');
        if (language.length() > 0) {
            temp.append(language);
            result.add(0, temp.toString());
        }

        temp.append('_');
        if (country.length() > 0) {
            temp.append(country);
            result.add(0, temp.toString());
        }

        if (variant.length() > 0 && (language.length() > 0 || country.length() > 0)) {
            temp.append('_').append(variant);
            result.add(0, temp.toString());
        }

        return result;
    }
}
