package com.github.lgdd.liferay.workshop.concert.collection.providers;

import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.info.list.provider.InfoListProvider;
import com.liferay.info.list.provider.InfoListProviderContext;
import com.liferay.info.pagination.Pagination;
import com.liferay.info.sort.Sort;
import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.search.query.Queries;
import com.liferay.portal.search.searcher.SearchRequestBuilderFactory;
import com.liferay.portal.search.searcher.Searcher;
import com.liferay.portal.search.sort.Sorts;
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

    // TODO implement the search request to Elasticsearch using Liferay Java APIs

    return Collections.emptyList();
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
