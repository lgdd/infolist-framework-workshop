package com.github.lgdd.liferay.workshop.concert.collection.providers;

import aQute.bnd.annotation.metatype.Meta;

@Meta.OCD(
    id = ConcertsInfoListProvidersConfiguration.PID,
    localization = "content/Language",
    name = "workshop.concert.collection.providers.config.config-name"
)
public interface ConcertsInfoListProvidersConfiguration {

  @Meta.AD(
      deflt = "0",
      required = false,
      name = "workshop.concert.collection.providers.config.structure-id-name",
      description = "workshop.concert.collection.providers.config.structure-id-desc"
  )
  int structureId();

  @Meta.AD(
      deflt = "0",
      required = false,
      name = "workshop.concert.collection.providers.config.asset-library-id-name",
      description = "workshop.concert.collection.providers.config.asset-library-id-desc"
  )
  int assetLibraryId();

  String PID = "com.github.lgdd.liferay.workshop.concert.collection.providers.ConcertsInfoListProvidersConfiguration";
}
