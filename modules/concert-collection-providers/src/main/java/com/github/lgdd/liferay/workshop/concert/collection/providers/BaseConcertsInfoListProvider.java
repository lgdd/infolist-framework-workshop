package com.github.lgdd.liferay.workshop.concert.collection.providers;

import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.info.list.provider.InfoListProvider;
import com.liferay.info.list.provider.InfoListProviderContext;
import com.liferay.info.pagination.Pagination;
import com.liferay.info.sort.Sort;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.hits.SearchHit;
import com.liferay.portal.search.query.DateRangeTermQuery;
import com.liferay.portal.search.query.Queries;
import com.liferay.portal.search.searcher.SearchRequest;
import com.liferay.portal.search.searcher.SearchRequestBuilder;
import com.liferay.portal.search.searcher.SearchRequestBuilderFactory;
import com.liferay.portal.search.searcher.SearchResponse;
import com.liferay.portal.search.searcher.Searcher;
import com.liferay.portal.search.sort.SortOrder;
import com.liferay.portal.search.sort.Sorts;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseConcertsInfoListProvider
    implements InfoListProvider<AssetEntry> {

  @Override
  public List<AssetEntry> getInfoList(InfoListProviderContext infoListProviderContext,
      Pagination pagination, Sort sort) {

    if (infoListProviderContext.getGroupOptional().isPresent()) {
      Group group = getConcertsGroup(infoListProviderContext.getGroupOptional().get());
      final long companyId = group.getCompanyId();
      final long groupId = group.getGroupId();
      final long structureId = getStructureId();
      final String defaultLanguageId = group.getDefaultLanguageId();

      log.info("Company ID: {}", companyId);
      log.info("Group ID: {}", groupId);
      log.info("Structure ID: {}", structureId);
      log.info("Default Language ID: {}", defaultLanguageId);

      try {
        ddmStructureLocalService.getDDMStructure(structureId);
      } catch (PortalException e) {
        log.info("Cannot fetch Web Content Structure with ID={}", structureId, e);
        return Collections.emptyList();
      }

      log.info("Looking for articles");

      String fieldName =
          DDM_KEYWORD + structureId + StringPool.DOUBLE_UNDERLINE + getFieldName()
              + StringPool.UNDERLINE + defaultLanguageId + STRING_SORTABLE;

      String startDate = getStartDate();
      String endDate = getEndDate();

      SearchRequest searchRequest = getSearchRequest(pagination, sort,
                                                     companyId,
                                                     groupId,
                                                     fieldName,
                                                     startDate,
                                                     endDate);

      SearchResponse searchResponse = searcher.search(searchRequest);

      List<SearchHit> searchHits = searchResponse.getSearchHits().getSearchHits();

      log.info("Found {} hits", searchHits.size());

      List<AssetEntry> entries = new ArrayList<>();

      searchHits.forEach(hit -> {
        log.info("Hit: {}", hit.getId());
        int beginIndex = hit.getId().lastIndexOf("_") + 1;
        long journalArticleId = Long.parseLong(hit.getId().substring(beginIndex));
        try {
          long resourcePrimKey = journalArticleLocalService.getArticle(journalArticleId)
                                                           .getResourcePrimKey();
          entries
              .add(
                  assetEntryLocalService.getEntry(JournalArticle.class.getName(), resourcePrimKey));
        } catch (PortalException e) {
          log.warn("Ignore JournalArticle with ID {}", journalArticleId, e);
        }
      });

      return entries;
    }

    return Collections.emptyList();
  }

  protected SearchRequest getSearchRequest(Pagination pagination, Sort sort, long companyId,
      long groupId, String fieldName, String startDate, String endDate) {

    DateRangeTermQuery dateRangeTermQuery = queries
        .dateRangeTerm(fieldName, true, true, startDate, endDate);

    SearchRequestBuilder searchRequestBuilder = searchRequestBuilderFactory.builder();
    searchRequestBuilder.emptySearchEnabled(true);

    if (Validator.isNotNull(sort)) {
      searchRequestBuilder.sorts(
          sorts.field(sort.getFieldName(), sort.isReverse() ? SortOrder.DESC : SortOrder.ASC));
    } else {
      searchRequestBuilder.sorts(sorts.field(fieldName, SortOrder.ASC));
    }

    if (Validator.isNotNull(pagination)) {
      searchRequestBuilder.from(pagination.getStart());
      searchRequestBuilder.size(pagination.getDelta());
    }

    searchRequestBuilder.withSearchContext(searchContext -> {
      searchContext.setCompanyId(companyId);
      searchContext.setGroupIds(new long[]{groupId});
    });

    return searchRequestBuilder
        .query(dateRangeTermQuery)
        .build();
  }

  @Override
  public List<AssetEntry> getInfoList(InfoListProviderContext infoListProviderContext) {

    return getInfoList(infoListProviderContext, null, null);
  }

  @Override
  public int getInfoListCount(InfoListProviderContext infoListProviderContext) {

    return getInfoList(infoListProviderContext, null, null).size();
  }

  @Override
  public String getLabel(Locale locale) {

    ResourceBundle resourceBundle =
        ResourceBundleUtil.getBundle("content.Language", locale, getClass());
    return LanguageUtil.get(resourceBundle, getLabelKey());
  }

  protected abstract String getLabelKey();

  protected abstract Group getConcertsGroup(Group defaultGroup);

  protected abstract long getStructureId();

  protected abstract String getFieldName();

  protected abstract String getStartDate();

  protected abstract String getEndDate();

  protected abstract void setDDMStructureLocalService(
      DDMStructureLocalService ddmStructureLocalService);

  protected abstract void setSearchRequestBuilderFactory(
      SearchRequestBuilderFactory searchRequestBuilderFactory);

  protected abstract void setQueries(Queries queries);

  protected abstract void setSorts(Sorts sorts);

  protected abstract void setSearcher(Searcher searcher);

  protected abstract void setAssetEntryLocalService(AssetEntryLocalService assetEntryLocalService);

  protected abstract void setJournalArticleLocalService(
      JournalArticleLocalService journalArticleLocalService);

  protected abstract void setDepotEntryLocalService(DepotEntryLocalService depotEntryLocalService);

  protected DDMStructureLocalService ddmStructureLocalService;
  protected SearchRequestBuilderFactory searchRequestBuilderFactory;
  protected Queries queries;
  protected Sorts sorts;
  protected Searcher searcher;
  protected AssetEntryLocalService assetEntryLocalService;
  protected JournalArticleLocalService journalArticleLocalService;
  protected DepotEntryLocalService depotEntryLocalService;

  protected static final String STRING_SORTABLE = "_String_sortable";
  protected static final String CONCERT_DATE = "concertDate";
  protected static final String DDM_KEYWORD = "ddm__keyword__";
  protected static final String VERY_OLD_DATE = "1900-01-01";
  protected static final String DATE_IN_THE_FAR_FUTURE = "3000-01-01";

  private static final Logger log = LoggerFactory.getLogger(BaseConcertsInfoListProvider.class);
}
