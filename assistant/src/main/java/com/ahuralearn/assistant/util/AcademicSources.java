package com.ahuralearn.assistant.util;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HtmlUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.ahuralearn.assistant.domain.vo.SourceVO;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * Looks up real academic references for a query from the free Crossref API
 * ({@code https://api.crossref.org/works}, no API key). Used to show credible
 * verification sources for the research assistant instead of model-invented
 * citations.
 * <p>
 * Best-effort: any failure (network, rate limit, parse) is swallowed and an empty
 * list is returned, so the assistant's answer still loads without sources.
 *
 * @author Dariush
 * @since 2026-06-29
 */
@Slf4j
public final class AcademicSources {

    private static final String CROSSREF_WORKS = "https://api.crossref.org/works";
    private static final int TIMEOUT_MS = 8000;

    private AcademicSources() {
    }

    /**
     * Find up to {@code limit} real papers matching the query, newest-relevance first.
     * @return a list of {title, url} sources; empty if the lookup fails or finds nothing
     */
    public static List<SourceVO> search(String query, int limit) {
        List<SourceVO> sources = new ArrayList<>();
        if (StrUtil.isBlank(query))
            return sources;
        try {
            String body = HttpUtil.createGet(CROSSREF_WORKS)
                    .form("query", query)
                    .form("rows", limit)
                    .form("select", "title,URL")
                    // Crossref asks callers to identify themselves in the User-Agent
                    .header("User-Agent", "AhuraLearn/1.0 (academic research assistant)")
                    .timeout(TIMEOUT_MS)
                    .execute()
                    .body();

            JSONArray items = JSONUtil.parseObj(body).getJSONObject("message").getJSONArray("items");
            for (int i = 0; i < items.size() && sources.size() < limit; i++) {
                JSONObject item = items.getJSONObject(i);
                JSONArray titles = item.getJSONArray("title");
                String url = item.getStr("URL");
                if (titles == null || titles.isEmpty() || StrUtil.isBlank(url))
                    continue;
                // Crossref titles can contain HTML entities (e.g. &amp;)
                String title = HtmlUtil.unescape(titles.getStr(0)).trim();
                if (!title.isEmpty())
                    sources.add(new SourceVO(title, url));
            }
        } catch (Exception e) {
            log.warn("Crossref source lookup failed for query '{}': {}", query, e.getMessage());
        }
        return sources;
    }
}
