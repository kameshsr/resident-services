package io.mosip.resident.dto;

import com.fasterxml.jackson.annotation.JsonValue;
import io.mosip.resident.constant.IdAuthConfigKeyConstants;
import org.springframework.core.env.Environment;

import javax.xml.bind.annotation.XmlValue;
import java.util.EnumMap;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * General-purpose annotation used for configuring details of user
 * identification.
 * 
 * @author Rakesh Roshan
 * @author Loganathan Sekar
 */
public enum IdTypeEnum {

	/** The uin. */
	UIN("UIN"),
	/** The vid. */
	VID("VID")
	;

	/**
	 * Value that indicates that default id.
	 */
	public static final IdTypeEnum DEFAULT_ID_TYPE = IdTypeEnum.UIN;

	/** The type. */
	private String type;

	/** The aliases map. */
	private static EnumMap<IdTypeEnum, String> aliasesMap = new EnumMap<>(IdTypeEnum.class);

	/**
	 * construct enum with id-type.
	 *
	 * @param type id type
	 */
	private IdTypeEnum(String type) {
		this.type = type;
	}

	/**
	 * get id-type.
	 * 
	 * @return type
	 */
	@JsonValue
	@XmlValue
	public String getType() {
		return type;
	}

	/* (non-Javadoc)
	 * @see java.lang.Enum#toString()
	 */
	@Override
	public String toString() {
		return getType();
	}

	/**
	 * Look for id type or alias either "UIN" or "VID" or "USERID". default id is "UIN"
	 * 
	 * @param typeOrAlias String id-type or alias
	 * @return IdTypeEnum Optional with IdTypeEnum
	 */
	public static Optional<IdTypeEnum> getIdTypeEnum(String typeOrAlias) {
		if(typeOrAlias == null || typeOrAlias.trim().isEmpty()) {
			return Optional.empty();
		}
		
		return Stream.of(values())
				.filter(t -> t.getType().equalsIgnoreCase(typeOrAlias)
							|| t.getAlias().filter(typeOrAlias::equalsIgnoreCase).isPresent())
				.findAny();

	}
	
	/**
	 * Gets the ID type or default.
	 *
	 * @param type the type
	 * @return the ID type or default
	 */
	public static IdTypeEnum getIdTypeEnumOrDefault(String type) {
		return getIdTypeEnum(type).orElse(DEFAULT_ID_TYPE);

	}
	
	/**
	 * Gets the ID type str or default.
	 *
	 * @param type the type
	 * @return the ID type str or default
	 */
	public static String getIdTypeEnumStrOrDefault(String type) {
		return getIdTypeEnum(type).orElse(DEFAULT_ID_TYPE).getType();
	}
	
	/**
	 * Gets the ID type str or same str.
	 *
	 * @param type the type
	 * @return the ID type str or same str
	 */
	public static String getIdTypeEnumStrOrSameStr(String type) {
		return getIdTypeEnum(type).map(IdTypeEnum::getType).orElse(type);
	}
	
	/**
	 * Gets the alias str or same str.
	 *
	 * @param type the type
	 * @return the alias str or same str
	 */
	public static String getAliasStrOrSameStr(String type) {
		return getIdTypeEnum(type).flatMap(IdTypeEnum::getAlias).orElse(type);
	}
	
	/**
	 * Initialize aliases.
	 *
	 * @param env the env
	 */
	public static void initializeAliases(Environment env) {
		for(IdTypeEnum IdTypeEnum: IdTypeEnum.values()) {
			String aliasPropertyKey = String.format(IdAuthConfigKeyConstants.ID_TYPE_ALIAS, IdTypeEnum.getType().toLowerCase());
			String alias = env.getProperty(aliasPropertyKey, "").trim();
			if(!alias.isEmpty()) {
				aliasesMap.put(IdTypeEnum, alias);
			}
		}
	}
	
	/**
	 * Gets the alias.
	 *
	 * @return the alias
	 */
	public Optional<String> getAlias() {
		return Optional.ofNullable(aliasesMap.get(this));
	}
	
	/**
	 * Gets the alias or type.
	 *
	 * @return the alias or type
	 */
	public String getAliasOrType() {
		return getAlias().orElse(getType());
	}
}
