package com.github.lgdd.liferay.workshop.concert.collection.providers;

import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.info.list.provider.InfoListProvider;
import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.search.query.Queries;
import com.liferay.portal.search.searcher.SearchRequestBuilderFactory;
import com.liferay.portal.search.searcher.Searcher;
import com.liferay.portal.search.sort.Sorts;
import java.text.SimpleDateFormat;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(
    immediate = true,
    configurationPid = ConcertsInfoListProvidersConfiguration.PID,
    service = InfoListProvider.class
)
public class PastConcertsInfoListProvider
    extends BaseConcertsInfoListProvider {

  @Activate
  @Modified
  public void activate(Map<String, String> properties) {

    config = ConfigurableUtil
        .createConfigurable(ConcertsInfoListProvidersConfiguration.class, properties);
  }


  @Override
  protected String getLabelKey() {

    return "workshop.concert.collection.providers.past-concerts";
  }

  @Override
  protected Group getConcertsGroup(Group defaultGroup) {

    try {
      if (config.assetLibraryId() > 0) {
        return depotEntryLocalService.getDepotEntry(config.assetLibraryId()).getGroup();
      }
    } catch (PortalException e) {
      log.warn(e.getMessage(), e);
    }
    return defaultGroup;
  }

  @Override
  protected long getStructureId() {

    return config.structureId();
  }

  @Override
  protected String getFieldName() {

    return CONCERT_DATE;
  }

  @Override
  protected String getStartDate() {

    return VERY_OLD_DATE;
  }

  @Override
  protected String getEndDate() {

    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    Date today = new Date();
    Date yesterday = Date.from(today.toInstant().minus(1, ChronoUnit.DAYS));
    return format.format(yesterday);
  }

  @Reference(unbind = "-")
  @Override
  protected void setDDMStructureLocalService(DDMStructureLocalService ddmStructureLocalService) {

    this.ddmStructureLocalService = ddmStructureLocalService;
  }

  @Reference(unbind = "-")
  @Override
  protected void setSearchRequestBuilderFactory(
      SearchRequestBuilderFactory searchRequestBuilderFactory) {

    this.searchRequestBuilderFactory = searchRequestBuilderFactory;

  }

  @Reference(unbind = "-")
  @Override
  protected void setQueries(Queries queries) {

    this.queries = queries;

  }

  @Reference(unbind = "-")
  @Override
  protected void setSorts(Sorts sorts) {

    this.sorts = sorts;

  }

  @Reference(unbind = "-")
  @Override
  protected void setSearcher(Searcher searcher) {

    this.searcher = searcher;

  }

  @Reference(unbind = "-")
  @Override
  protected void setAssetEntryLocalService(AssetEntryLocalService assetEntryLocalService) {

    this.assetEntryLocalService = assetEntryLocalService;

  }

  @Reference(unbind = "-")
  @Override
  protected void setJournalArticleLocalService(
      JournalArticleLocalService journalArticleLocalService) {

    this.journalArticleLocalService = journalArticleLocalService;

  }

  @Reference(unbind = "-")
  @Override
  protected void setDepotEntryLocalService(DepotEntryLocalService depotEntryLocalService) {

    this.depotEntryLocalService = depotEntryLocalService;

  }

  private volatile ConcertsInfoListProvidersConfiguration config;
  private static final Logger log = LoggerFactory.getLogger(PastConcertsInfoListProvider.class);

}
