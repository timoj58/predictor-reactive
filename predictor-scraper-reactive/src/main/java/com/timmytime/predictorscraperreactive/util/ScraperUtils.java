package com.timmytime.predictorscraperreactive.util;

import org.apache.commons.lang.StringEscapeUtils;
import org.jsoup.parser.Parser;

import java.util.function.UnaryOperator;


public class ScraperUtils {

    public static UnaryOperator<String> format = data ->
            Parser.unescapeEntities(
                    StringEscapeUtils.escapeHtml(data).replace("&nbsp;", "")
                    , Boolean.TRUE
            ).trim();

}
