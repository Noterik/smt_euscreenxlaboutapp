package org.springfield.lou.application.types;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import org.json.simple.JSONObject;
import org.springfield.fs.FSListManager;
import org.springfield.fs.Fs;
import org.springfield.fs.FsNode;

/**
 * Creates a json.simple object holding the number of all videos, images, texts,
 * sounds, and series found in the EUScreen app's DB according to their country
 * of origin and provider.
 * 
 * All data retrieved from the DB is in regard to the EUScreen web app.
 * 
 * <p>
 * It uses Java "Locale" class to create a map of all countries and their codes
 * for the purpose of indexing the json structure.
 * </p>
 * 
 * @author roman
 *
 */
public class DynamicMapContent {
	// Map holding all country and country codes
	final static Map<String, String> countryCodes = 
			new TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER);
	
	final static int HOUR = 60;
	final static int DAY = 24;
	final static int ONE_HOUR = 1;
	
	// JSON variables holding all required nodes
	static JSONObject mapObj;
	static JSONObject countryNode;
	static JSONObject providersNode;
	static JSONObject providerNode;

	/**
	 * Generates the json object and sends it for insertion
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static JSONObject getJsonMapData() {
		List<FsNode> nodes = FSListManager.get("/domain/euscreenxl/user/*/*")
				.getNodes();

		// Loads all country codes into a map
		loadCountryCodes();
		Map<String, String> providerNamesMap = addProviderNames();

		// Creates the JSON to be stored as a new node inside the "cms" user
		mapObj = new JSONObject();

		// Adds all retrieved data to JSON
		for (FsNode node : nodes) {
			String country = node
					.getProperty("SpatioTemporalInformation_SpatialInformation"
							+ "_CountryofProduction");

			// Default value for country code if country name
			// is retrieved as null from the DB
			String countryCode = null;

			// Eliminates data if country name is null
			if (country != null) {
				countryCode = getCountryCode(country);
			}

			// If provider is "Deutsche Welle" it replaces it
			// with "DW" for consistency.
			String provider = node.getProperty("provider");
			if (provider != null && provider.equalsIgnoreCase("Deutsche Welle")) {
				provider = "DW";
			} 
			
			if (provider != null && provider.equalsIgnoreCase("Netherlands Institute for Sound and Vision")) {
				provider = "NISV";
			} 
			
			if (provider != null && provider.equalsIgnoreCase("Narodowy Instytut Audiowizualny")) {
				provider = "NINA";
			} 

			// Add to record if there is a valid country and provider info
			if (countryCode != null && provider != null) {
				if (!mapObj.containsKey(countryCode)) {
					countryNode = new JSONObject();
					countryNode.put("id", countryCode);
					countryNode.put("country", country);

					providerNode = 
							initProviderNode(providerNamesMap.get(provider.toUpperCase()));
					
					providersNode = new JSONObject();
					providersNode.put(provider, providerNode);

					countryNode.put("providers", providersNode);

				} else {
					countryNode = (JSONObject) mapObj.get(countryCode);
					providersNode = (JSONObject) countryNode.get("providers");

					if (!providersNode.containsKey(provider)) {
						providerNode = 
								initProviderNode(providerNamesMap.get(provider.toUpperCase()));
						
						providersNode.put(provider, providerNode);
					}

					providerNode = (JSONObject) providersNode.get(provider);
					
					// Increments the total number of videos, images and etc.
					// to the specific provider
					if (node.getProperty("TechnicalInformation_materialType") != null) {
						if (node.getProperty(
								"TechnicalInformation_materialType")
								.equalsIgnoreCase("VIDEO")) {
							providerNode.put("videos",
									(Integer) providerNode.get("videos") + 1);

						} else if (node.getProperty(
								"TechnicalInformation_materialType")
								.equalsIgnoreCase("IMAGE")) {
							providerNode.put("images",
									(Integer) providerNode.get("images") + 1);

						} else if (node.getProperty(
								"TechnicalInformation_materialType")
								.equalsIgnoreCase("TEXT")) {
							providerNode.put("texts",
									(Integer) providerNode.get("texts") + 1);

						} else if (node.getProperty(
								"TechnicalInformation_materialType")
								.equalsIgnoreCase("SOUND")) {
							providerNode.put("audios",
									(Integer) providerNode.get("audios") + 1);

						} else {
							providerNode.put("series",
									(Integer) providerNode.get("series") + 1);
						}
					}
					
					providersNode.put(provider, providerNode);
					countryNode.put("providers", providersNode);
				}

				mapObj.put(countryCode, countryNode);

			}
		}
		
		return mapObj;
	}

	/**
	 * Helper method that initiates the "providerNode" 
	 * inside the main JSON structure.
	 * 
	 * @param name
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static JSONObject initProviderNode(String name) {
		JSONObject providerNode = new JSONObject();

		providerNode.put("name", name);
		providerNode.put("videos", 0);
		providerNode.put("images", 0);
		providerNode.put("texts", 0);
		providerNode.put("audios", 0);
		providerNode.put("series", 0);

		return providerNode;
	}

	/**
	 * Creates a map of all countries found on record and their codes.
	 * 
	 */
	public static void loadCountryCodes() {
		for (String iso : Locale.getISOCountries()) {
			Locale locale = new Locale("", iso);
			countryCodes.put(locale.getDisplayCountry(), iso);
		}
	}

	/**
	 * Retrieves the country code from the map.
	 * 
	 * @param country
	 * @return
	 */
	public static String getCountryCode(String country) {
		String countryCode = countryCodes.get(country);
		if (countryCode == null) {
			countryCode = "Error retrieving country code!";
		}

		return countryCode;
	}

	/**
	 * Creates a new node with the most current info and inserts it into the DB
	 * 
	 */
	public static void insertMapNode() {
		// Prepare the node to be inserted
		FsNode mapNode = new FsNode("page", "map");
		Fs.insertNode(mapNode, "/domain/euscreenxl/user/cms");

		Date newTimeStamp = new Date();
		Long timeStamp = newTimeStamp.getTime();
		
		// Nested sub-node holding the actual data
		FsNode componentNode = new FsNode("component", "content-map");
		componentNode.setProperty("lastChange", timeStamp.toString());
		componentNode.setProperty("data", getJsonMapData().toString());
		Fs.insertNode(componentNode, "/domain/euscreenxl/user/cms/page/map");
		
		System.out.println("EUScreen's dynamic map content has been updated!");
	}

	/**
	 * Checks if it was more than 24-hours since the last update of the map data,
	 * and if so generates a new node with the most current info, which is later 
	 * added to the DB.
	 * 
	 */
	public static void getLastChange() {
		FsNode node = 
			Fs.getNode("/domain/euscreenxl/user/cms/page/map/component/content-map");
		
		// Get the time of the last change in the DB
		if (node != null) {
			long currentTime = new Date().getTime();
			if(node.getProperty("lastChange") != null){
				Long timeDiff = currentTime-Long.parseLong(node.getProperty("lastChange"));
				
				// Printing some info to the console for testing
				final Calendar cal = Calendar.getInstance();
				cal.setTimeInMillis(Long.parseLong(node.getProperty("lastChange")));
				System.out.println("-----------EUScreen Map Content-------------");
				System.out.println("Last change made: "
					+new SimpleDateFormat("dd-M-yyyy hh:mm:ss").format(cal.getTime()));
				
				long toMinutes = TimeUnit.MILLISECONDS.toMinutes(timeDiff);		
				if(toMinutes/HOUR > ONE_HOUR) {
					System.out.println("Since last change: " +toMinutes/HOUR+"-hours");
				} else
					System.out.println("Since last change: "+toMinutes+"-minutes");
				
				// If it was more than 24-hours update the data and insert into the DB
				if(toMinutes/HOUR > DAY) {
					insertMapNode();
					System.out.println("It has been more than 24-hours since last update!");
				}
				System.out.println("--------------------------------------------");
			}else{
				insertMapNode();
			}

		} else {
			
			System.out.println("Node is null!");
		}
	}
	
	/**
	 * Adds all known/existing provider names to a map
	 * used later on for adding the names to the providerNode
	 * inside the JSONObject
	 * 
	 * @return
	 */
	public static Map<String, String> addProviderNames() {
		Map<String, String> providerNamesMap = new TreeMap<String, String>();
		
		providerNamesMap.put("BBC", "British Broadcasting Corporation");
		providerNamesMap.put("SASE", "Screen Archive South East");
		providerNamesMap.put("ERT SA", "Ellinik&iacute; Radiofon&iacute;a Tile&oacute;rasi");
		providerNamesMap.put("HeNAA", "Hellenic National Audiovisual Archive");
		providerNamesMap.put("CT", "&#268;eská Televize");
		providerNamesMap.put("LCVA", "Lietuvos Centrinis Valstyb&ecirc;s Archyvas");
		providerNamesMap.put("NINA", "Narodowy Instytut Audiowizualny");
		providerNamesMap.put("MEMORIAV", "Memoriav-Association pour la sauvegarde de la m&eacutemoire audiovisuelle suisse");
		providerNamesMap.put("ORF", "&Ouml;sterreichischer Rundfunk");
		providerNamesMap.put("TVC", "Televisi&oacute; de Catalunya");
		providerNamesMap.put("TVR", "Televiziunea Rom&acirc;n&#259;");
		providerNamesMap.put("INA", "Institut national de l'audiovisuel");
		providerNamesMap.put("NISV", "Nederlands Instituut voor Beeld en Geluid");
		providerNamesMap.put("NAVA", "Nemzeti Audiovizu&aacute;lis Archivum");
		providerNamesMap.put("DR", "Danmarks Radio");
		providerNamesMap.put("DW", "Deutsche Welle");
		providerNamesMap.put("RTP","R&aacute;dio e Televis&atilde;o de Portugal");
		providerNamesMap.put("RTBF", "Radio-T&eacute;l&eacute;vision Belge de la Communaut&eacute; Fran&ccedil;aise");
		providerNamesMap.put("VRT", "Vlaamse Radio- en Televisieomroeporganisatie");
		providerNamesMap.put("RTV SLO", "Radiotelevizija Slovenija");
		providerNamesMap.put("RTÉ", "Raidi&oacute; Teilif&iacute;s &Eacute;ireann");
		providerNamesMap.put("LUCE", "Istituto Luce Cinecitt&agrave;");
		providerNamesMap.put("TVP", "Telewizja Polska");
		providerNamesMap.put("RAI", "Radiotelevisione Italiana S.p.A.");
		providerNamesMap.put("MEMORIAV-SRF", "Schweizer Radio und Fernsehen");
		providerNamesMap.put("MEMORIAV-RTS", "Radio T&eacute;l&eacute;vision Suisse");
		providerNamesMap.put("RH", "Royal Holloway");
		providerNamesMap.put("FINA", "Filmoteka Narodowa – Instytut Audiowizualny");
		
		return providerNamesMap;
		
	}
}
