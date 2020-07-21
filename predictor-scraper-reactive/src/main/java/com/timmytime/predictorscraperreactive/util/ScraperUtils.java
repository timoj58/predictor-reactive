package com.timmytime.predictorscraperreactive.util;

import org.apache.commons.lang.StringEscapeUtils;
import org.json.JSONObject;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;
import us.codecraft.xsoup.XElements;
import us.codecraft.xsoup.Xsoup;

import java.util.Iterator;
import java.util.function.BiFunction;
import java.util.function.UnaryOperator;


public class ScraperUtils {

    public static BiFunction<JSONObject, String, String> replace = (values, xpath) -> {

        Iterator keys = values.keys();

        while (keys.hasNext()) {
            String key = keys.next().toString();
            xpath = xpath.replace(key, values.get(key).toString());
        }

        return xpath;
    };

    public static UnaryOperator<String> format = data ->
            Parser.unescapeEntities(
                    StringEscapeUtils.escapeHtml(data).replace("&nbsp;", "")
                    , Boolean.TRUE
            ).trim();

    public static XElements compile(JSONObject object, String xpath, Document document) {
        return Xsoup.compile(
                replace.apply(
                        object,
                        xpath))
                .evaluate(document);
    }

}
