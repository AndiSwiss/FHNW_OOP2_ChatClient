package ch.andreasambuehl.achat.common;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.Logger;

/**
 * This class provides functionality for translations.
 *
 * @author Andreas Ambühl (with code fragments by Prof. Dr. Brad Richards)
 */
public class Translator {
    private ServiceLocator sl = ServiceLocator.getServiceLocator();
    private Logger logger = sl.getLogger();

    protected Locale currentLocale;
    private ResourceBundle resourceBundle;

    public Translator(String localeString) {
        // Can we find the language in our supported locales?
        // If not, use VM default locale
        Locale locale = Locale.getDefault();
        if (localeString != null) {

            /* todo: currently code completely copied from Brad Richards
             *  Change the comparing against the the Locale[] availableLocales
             *  to functional programming (with streams instead of saving in a
             *  variable and using a for-loop)
             *  OTHERWISE: change the class JavaDoc to "Copyright 2015...." as in 'ServiceLocator'.
             */

            Locale[] availableLocales = sl.getLocales();
            for (Locale availableLocale : availableLocales) {
                String tmpLang = availableLocale.getLanguage();
                if (localeString.substring(0, tmpLang.length()).equals(tmpLang)) {
                    locale = availableLocale;
                    break;
                }
            }
        }


        // Load the resource strings
        resourceBundle = ResourceBundle.getBundle(sl.getAPP_CLASS().getName(), locale);
        Locale.setDefault(locale); // Change VM default (for dialogs, etc.)
        currentLocale = locale;

        logger.info("Loaded resources for " + locale.getLanguage());
    }

    /**
     * Return the current locale; this is useful for formatters, etc.
     */
    public Locale getCurrentLocale() {
        return currentLocale;
    }

    /**
     * Public method to get string resources, default to "--" *
     */
    public String getString(String key) {
        try {
            return resourceBundle.getString(key);
        } catch (MissingResourceException e) {
            logger.warning("Missing string: " + key);
            return "--";
        }
    }
}
