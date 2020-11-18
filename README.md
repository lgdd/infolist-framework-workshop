# Liferay France Symposium - Liferay DXP 7.3 Workshop

L'objectif de ce workshop est d'illustrer les capacités de Liferay DXP 7.3
en termes de gestion de contenus et de présentation.

Nous mettrons en évidence les fonctionnalités suivantes :

 - **Asset Library** : Il s'agit d'une nouvelle entité qui permet de gérer
 des bibliothèques de contenus transverses, indépendantes des sites.
 Cependant, contrairement au site *Global*, il est possible d'avoir plusieurs
 asset libraries avec des administrateurs et auteurs distincts mais aussi de
 gérer finement leur visibilité à travers l'ensemble des sites du portail.
 
 
 - **Collection display** sur **Content pages** : Nous allons illustrer
 cette alternative à l'Asset Publisher dédié aux content pages. Contrairement
 à ce dernier, il permet de construire sa présentation sans avoir à écrire
 de code pour les templates.
 
 
 - **Collection providers** : Que ce soit pour les asset publishers ou les
 collection displays, on rencontre parfois des situations dans lesquelles
 on n'est pas en mesure de définir un filtre de contenu complexe directement
 via les settings du back-office Liferay. Dans ce cas, il est utile de
 s'appuyer sur cette capacité programmatique qui peut facilement
 exploiter les index Elasticsearch.

## Préparation

### Environnement local

Clonez ce repo sur votre poste de travail.
Si vous êtes sous windows, prenez garde à ne pas le télécharger dans un répertoire trop
profond afin d'éviter les risques de dépassement de la limite de longueur de chemin
absolu de fichier.

Placez-vous dans le dossier du repo téléchargé et exécutez la commande suivante pour télécharger
le bundle Liferay 7.3 (environ 991 Mo):

```
blade gw initBundle
# ou
./gradlew initBundle
# ou, sous windows
gradlew initBundle
```

Vous pouvez ensuite démarrer une instance locale de Liferay (assurez-vous que rien n'écoute notamment sur le port 8080) :

```
blade server start
# ou
./bundles/tomcat-9.0.37/bin/startup.sh
# ou, sous windows
.\bundles\tomcat-9.0.37\bin\startup.bat
```

---

**NOTE**

Si vous avez récemment fait tourner un Liferay 7.2 en localhost, il y a des chances que des CSS aient été mises en cache par votre navigateur.
Si c'est le cas, un *Ctrl+F5* est utile pour forcer le chargement des CSS de Liferay 7.3, sans quoi une partie des menus de navigation de Liferay serait un peu cassée.

---

Créer un site *Agenda des concerts*. C'est sur ce dernier qu'on fera la
conception de la présentation des évènements.

![](https://i.imgur.com/qqY8t2J.png)

Créer une asset library *Concerts*.

![](https://i.imgur.com/E1dhVo3.png)

Nous allons notamment nous en servir pour créer des *Web Content* et les rendre disponibles dans les sites via des Collections.

![](https://i.imgur.com/RDHQe4i.png)

Dans la rubrique *settings* nous allons maintenant connecter cette asset library avec notre site afin que ses contenus et les structures y soient visibles :

![](https://i.imgur.com/m8GDnla.png)




---

**ATTENTION**

Nous avons choisi de vous montrer un exemple de recherche dans l'index qui fonctionnera à la fois dans Liferay 7.2 et 7.3 pour l'InfoListProvider.

C'est pourquoi avant toute chose, commencez par aller dans *System Settings -> Dynamic Data Mapping* et cochez **Enable Legacy Dynamic Data Mapping Index Fields**.

![](https://i.imgur.com/AVU9SWK.png)

![](https://i.imgur.com/lskoTBQ.png)


Liferay a décidé de changer la manière d'indexer les contenus en regroupant dans une unique colonne `ddmFieldArray` d'index Elasticsearch tous les DDMField des Web content structures là où auparavant on avait une colonne par DDMField.

Notre code s'appuie sur l'ancienne indexation.

---


### Création de la structure de web content *Concert*

Créer une structure concert avec :
 - un artiste
 - une date
 - une description
 - une image (affiche)
 - le nom de la ville
 - un media pour le trailer
 - un media pour l’enregistrement du concert pour le replay
 - une URL du stream live si l'évènement est retransmis en direct

Pour gagner du temps et pour que vous puissiez utiliser notre code tel quel, nous vous proposons de copier coller cette structure dans l'édition de **Web content structure** :

```json
{
  "availableLanguageIds": [
    "en_US"
  ],
  "defaultLanguageId": "en_US",
  "fields": [
    {
      "label": {
        "en_US": "Concert Date"
      },
      "predefinedValue": {
        "en_US": ""
      },
      "style": {
        "en_US": ""
      },
      "tip": {
        "en_US": ""
      },
      "dataType": "date",
      "fieldNamespace": "ddm",
      "indexType": "keyword",
      "localizable": true,
      "name": "concertDate",
      "readOnly": false,
      "repeatable": false,
      "required": false,
      "showLabel": true,
      "type": "ddm-date"
    },
    {
      "label": {
        "en_US": "Artist"
      },
      "predefinedValue": {
        "en_US": ""
      },
      "style": {
        "en_US": ""
      },
      "tip": {
        "en_US": ""
      },
      "dataType": "string",
      "indexType": "keyword",
      "localizable": true,
      "name": "artist",
      "readOnly": false,
      "repeatable": false,
      "required": false,
      "showLabel": true,
      "type": "text"
    },
    {
      "label": {
        "en_US": "Picture"
      },
      "predefinedValue": {
        "en_US": ""
      },
      "style": {
        "en_US": ""
      },
      "tip": {
        "en_US": ""
      },
      "dataType": "image",
      "fieldNamespace": "ddm",
      "indexType": "text",
      "localizable": true,
      "name": "picture",
      "readOnly": false,
      "repeatable": false,
      "required": false,
      "showLabel": true,
      "type": "ddm-image"
    },
    {
      "label": {
        "en_US": "Description"
      },
      "predefinedValue": {
        "en_US": ""
      },
      "style": {
        "en_US": ""
      },
      "tip": {
        "en_US": ""
      },
      "dataType": "string",
      "indexType": "text",
      "localizable": true,
      "name": "description",
      "readOnly": false,
      "repeatable": false,
      "required": false,
      "showLabel": true,
      "type": "textarea"
    },
    {
      "label": {
        "en_US": "Place"
      },
      "predefinedValue": {
        "en_US": ""
      },
      "style": {
        "en_US": ""
      },
      "tip": {
        "en_US": ""
      },
      "dataType": "string",
      "indexType": "keyword",
      "localizable": true,
      "name": "place",
      "readOnly": false,
      "repeatable": false,
      "required": false,
      "showLabel": true,
      "type": "text"
    },
    {
      "label": {
        "en_US": "Trailer URL"
      },
      "predefinedValue": {
        "en_US": ""
      },
      "style": {
        "en_US": ""
      },
      "tip": {
        "en_US": ""
      },
      "dataType": "string",
      "indexType": "keyword",
      "localizable": true,
      "name": "trailerUrl",
      "readOnly": false,
      "repeatable": false,
      "required": false,
      "showLabel": true,
      "type": "text"
    },
    {
      "label": {
        "en_US": "Stream URL"
      },
      "predefinedValue": {
        "en_US": ""
      },
      "style": {
        "en_US": ""
      },
      "tip": {
        "en_US": ""
      },
      "dataType": "string",
      "indexType": "keyword",
      "localizable": true,
      "name": "streamUrl",
      "readOnly": false,
      "repeatable": false,
      "required": false,
      "showLabel": true,
      "type": "text"
    },
    {
      "label": {
        "en_US": "Recording URL"
      },
      "predefinedValue": {
        "en_US": ""
      },
      "style": {
        "en_US": ""
      },
      "tip": {
        "en_US": ""
      },
      "dataType": "string",
      "indexType": "keyword",
      "localizable": true,
      "name": "recordingUrl",
      "readOnly": false,
      "repeatable": false,
      "required": false,
      "showLabel": true,
      "type": "text"
    }
  ]
}
```

### Alimentation de l'asset library

Créer quelques enregistrements dans cette *asset library*.

Pensez à créer des évènements dans le passé, dans le futur mais aussi des évènements le jour du symposium, c'est à dire aujourd'hui, le 18 novembre 2020. Nous avons préparé des streams en direct à inclure pour ces derniers. **Nous vous fournirons les URLs dans le chat du workshop.**

Si vous êtes en manque d'inspiration, vous pouvez vous inspirer des évènements programmés ici : https://www.arte.tv/fr/videos/RC-014346/arte-concert-festival/

### Création de la collection dynamique des concerts

Nous allons maintenant créer une collection dynamique de concerts.

Une *collection* est définie de façon assez similaire aux paramètres d'un *asset publisher* avec sélection dynamique des éléments.

On va donc cibler ici les web content articles dont la structure est *Concert* et définir le
scope, c'est-à-dire les emplacements à partir desquels seront recherchés les contenus dynamiques.

![](https://i.imgur.com/1HkAQvQ.png)

Pour le moment, nous allons lister tous les concerts, quelle que soit la date. Il peut être utile toutefois d'implémenter un tri sur la date pour les afficher dans un ordre cohérent.

---

**NOTE**

C'est probablement une limite que vous connaissez déjà dans les asset publishers : bien qu'il soit possible de paramétrer des filtres sur des champs d'une structure de web content, seul le test d'égalité est possible.

Dans notre cas, on aurait pu vouloir filtrer uniquement les concerts passés ou à venir. Ce n'est pas possible directement en passant par ici : c'est ce qui va nécessiter un peu plus loin dans cet atelier qu'on construise un *Collection provider* programmatique.

---

### Création d'une collection manuelle

Il peut être pertinent de constituer une sélection éditoriale de concerts qu'on souhaite mettre en avant. A cet effet, on peut créer une collection de type manuelle et y sélectionner à la main les éléments qu'on souhaite y intégrer.

![](https://i.imgur.com/RkwniCQ.png)

Ces listes sélectionnées à la main pourraient être utilisées pour afficher un bandeau de quelques concerts choisis.

![](https://i.imgur.com/9S6QfPw.png)

---

**NOTE**

Lorsque vous créez une content page, il est possible d'en créer des variantes qu'on appelle **Experiences**. Ces expériences peuvent être activées en fonction d'un **Segment** de l'utilisateur qui visite la page. Cela permet ainsi d'afficher des recommandations personnalisées au visiteur.

---

## Construction du site

Maintenant que nous disposons d'une collection de concerts, nous allons illustrer comment nous pouvons les exploiter depuis un site pour les afficher.

### Content pages et *Collections Display*

Créer une content page *Programmation* sur le site de l'agenda des concerts.

![](https://i.imgur.com/M8eGo10.png)


![](https://i.imgur.com/od7CaNr.png)

---

**NOTE**

Dans Liferay DXP 7.3, en sélectionnant *Blank page*, on a dorénavant une *Content page* par défaut.
Le choix de la *widget page*, est une option qui apparait maintenant dans les choix de types de pages complémentaires

---

Passer en mode édition dans le menu de droite et ajouter un item *Collections display*.

Commencez par construire la structure de votre page avec les éléments disponibles ici et prévoyez
un emplacement pour le **Collection Display** :

![](https://i.imgur.com/8jSKhqK.png)

Vous pouvez ensuite, en cliquant sur la collection, paramétrer sa configuration :

![](https://i.imgur.com/gl0GHve.png)

La première étape consiste à choisir la Collection sur la base de laquelle s'appuyer, nous allons donc utiliser notre **collection de concerts**.

Le *Collections display* permet de définir sous quelle forme la liste des éléments est présentée puis, pour chaque élément de la liste, permet de constituer son template en **assemblant des éléments d'interface graphique unitaires** (titres, liens, images, paragraphes...) et de **mapper leurs contenus** sur des champs de la structure du contenu qui est mappé (exemple : la date du concert).

![](https://i.imgur.com/5FGD2ck.png)

Par exemple, je décide d'afficher chaque élément sous forme d'une grille horizontale, constituée de plusieurs *Modules*. Dans chaque module, je peux ainsi insérer des éléments d'UI dont je peux mapper les parties dynamiques comme cet *element-text*.

![](https://i.imgur.com/EHkgGFo.png)

### Asset publisher

Il y a des limites dans la configuration des collection displays et l'asset publisher constituera parfois une solution plus puissantes dans l'agrégation de contenus via notamment :

 - La gestion de la pagination
 - L'intégration avec l'asset categories navigation portlet pour le filtrage de ses entrées
 - L'application de templates freemarker plus complexes

Créez une nouvelle page de type *widget page* et posez un asset publisher. Sélectionnez la collection des concerts.

## Filtrage dynamique avec InfoListProvider

Nous avons vu que les collections dynamiques étaient limitées dans leurs capacités de filtrage de contenus.

### Programmation des InfoListProviders

Nous allons utiliser l'API Liferay `InfoListProvider` pour construire trois collections dynamiques :

 - La liste des **concerts passés** (pour permettre aux visiteurs d'accéder au replay)
 - La liste des **concerts à venir** (avec présentation d'un trailer)
 - La liste des **concerts du jour, en cours de streaming**

Pour réaliser ces InfoListProviders, nous nous appuyons sur le fait que nous avons rendu **indexable** la *date du concert* dans la *structure concert* de façon à pouvoir faire une requête de sélection performante pour restituer dynamiquement ces trois listes.

Afin de mutualiser le code commun à ces trois providers, nous avons constitué une classe abstrait `com.github.lgdd.liferay.workshop.concert.collection.providers.BaseConcertsInfoListProvider` à étendre pour implémenter les trois `InfoListProvider`.

L'objectif du workshop sera de **finaliser le code de cette classe abstraite**.

Voici pour exemple le code de celui qui fournit la liste des concerts passés (cela consiste à fixer les critères de date et à nommer la collection telle qu'elle apparaîtra dans les champs de sélection du back-office Liferay):

```java=
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
```

---

**NOTE**

Bien que les `@Reference` vers les services OSGi sont déjà utiles au niveau de la classe abstraite, il est important de noter que l'annotation `@Reference` n'a de sens que dans un `@Component`. Et comme une classe abstraite n'est pas un `@Component`, ce n'est que dans les classes concrètes qu'on peut binder la `@Reference`.

---

Il reste à implémenter la méthode de `com.github.lgdd.liferay.workshop.concert.collection.providers.BaseConcertsInfoListProvider` qui interroge les
API de recherche afin de retourner les enregistrements de la **structure** concert de l'**asset library** events dont les IDs sont fournis
par une configuration dans les System settings.

Vous pouvez suivre l'implémentation en cours de workshop ou observer le diff : https://github.com/lgdd/infolist-framework-workshop/pull/1/commits/c12b264b5f8d5b2919d16dc150fd7c5b948b6562

### Configuration des InfoListProviders

Notre module a besoin de connaître les IDs de la Web Content Structure
des concerts et de l'asset library des évènements.

Une fois que vous avez identifié ces IDs dans le back-office Liferay,
vous pourrez les renseigner ici :

![](https://i.imgur.com/Fild8KA.png)


### Intégration des InfoListProviders

Nous allons constituer une nouvelle **Collection Display** sur notre page *agenda des concerts* qui
listera les évènements passés sous la forme d'un tableau avec le lien vers son enregistrement.

![](https://i.imgur.com/s3WyePI.png)

Et nous allons ensuite constituer un **bandeau** chargé de lister les concerts en cours de streaming.

Afin de présenter la vidéo du flux live dans les éléments de ce bandeau, nous allons créer un fragment via le menu de gauche *Design -> Fragments*.

La première étape consiste à créer une collection de fragments. Dans cette collection nous allons créer un fragment *Live Concert*.

![](https://i.imgur.com/Uyn5cRn.png)

![](https://i.imgur.com/NElOeN8.png)

L'édition d'un fragment (blocs de code sous le screenshot) :

![](https://i.imgur.com/JAmTtfP.png)

Il contiendra une partie *configuration* dans lequel on définit un sélecteur de permission d'affichage en plein écran (pour autoriser ou non l'iframe à passer la vidéo en plein écran) :

```json=
{
  "fieldSets": [
    {
      "fields": [
        {
          "defaultValue": false,
          "label": "Allow Full Screen",
          "name": "allowFullScreen",
          "type": "checkbox"
        }
      ]
    }
  ]
}
```

Et il contiendra le code HTML suivant (notez d'une part l'usage de l'objet `configuration` ainsi que l'usage de code `freemarker` pour l'ajout conditionel de l'attribut `allowfullscreen`) :

```htmlmixed=
<div class="fragment_iframe">
  <div>
    <a
      class="btn btn-primary my-video-link"
      data-lfr-editable-id="link"
      data-lfr-editable-type="link"
      href=""
      id="fragment-${fragmentEntryLinkNamespace}-link"
      onclick="return startRecord(this)"
    >
      View live record
    </a>
	
	<div class="embed-responsive embed-responsive-16by9 my-video-iframe-container my-invisible-iframe-container">
      <iframe  
        id="fragment-${fragmentEntryLinkNamespace}-iframe"
        class="my-video-iframe embed-responsive-item"
        [#if configuration.allowFullScreen == true] allowfullscreen [/#if]
        src="">
      </iframe>
	</div>
  </div>
</div>
```

Le bloc Javascript contient cette fonction pour lancer la vidéo :

```javascript=
window.startRecord = function(myVideoLink) {
  
	my_element = myVideoLink.parentElement;
	
    myVideoIframeContainer = my_element.querySelector('.my-video-iframe-container');
	myVideoIframeContainer.classList.remove("my-invisible-iframe-container");
	
	myVideoLink = my_element.querySelector('.my-video-link');
  
	myVideoIframe = my_element.querySelector('.my-video-iframe');
    myVideoIframe.src = myVideoLink.href;
	
	myVideoLink.classList.add("my-invisible-link");
	
    return false;

};
```

Et ce bloc CSS :

```css=
.fragment_iframe .my-invisible-iframe-container {
    display: none;
}

.fragment_iframe .my-invisible-link {
    display: none;
}
```

Vous pourrez alors utiliser ce fragment de cette manière en mappant la Stream URL
de la structure concert vers le link du Live concert :

![](https://i.imgur.com/J4I05UF.png)

Il est assez aisé de construire sa propre présentation en assemblant les fragments de base sur la content page.

Si vous êtes arrivés jusqu'ici, profitez du temps qui vous reste pour tester les possibilités de mise en page et n'hésitez pas à revenir vers nous avec vos questions.