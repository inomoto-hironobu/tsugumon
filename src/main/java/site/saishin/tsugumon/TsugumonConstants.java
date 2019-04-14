package site.saishin.tsugumon;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

public final class TsugumonConstants {
	private TsugumonConstants() {}
	
	public static final String ACCESS_MANAGER_NAME = "access_manager";
	public static final int MAX_SELECT_ANSWER_SIZE = 10;
	public static final int MAX_DESCRIPTION_SIZE = 500;
	public static final int MAX_ENTRY_STRING_SIZE = 50;
	public static final int MAX_ENTRIES_SIZE = 64;
	public static final int MAX_ENQUETE_JSON_SIZE = 5000;
	public static final int MAX_PAGE_SIZE = 30;
	
	public static final int SHORT_CYCLE_MINUTES = 12;
	public static final int MIDDLE_CYCLE_MINUTES = 2 * 60;
	public static final int LONG_CYCLE_MINUTES = 24 * 60;
	
	//
	public static final int LONG_TO_APP_LIMIT = 24 * 60 * 60;
	public static final int MIDDLE_TO_LONG_LIMIT = 60 * 60;
	public static final int SHORT_TO_MIDDLE_LIMIT = 60 * 12;
	
	public static final int SHORT_EXPIRATION = 10;
	public static final int MIDDLE_EXPIRATION = 360;
	public static final int LONG_EXPIRATION = 3600;
	
	public static final Response OK_RESPONSE = Response.ok().build();
	public static final Response CREATED_RESPONSE = Response.status(Status.CREATED).build();
	public static final Response BAD_REQUEST_RESPONSE = Response.status(Status.BAD_REQUEST).build();
	public static final Response NOT_FOUND_RESPONSE = Response.status(Status.NOT_FOUND).build();
	public static final Response FORBIDDEN_RESPONSE = Response.status(Status.FORBIDDEN).build();
	public static final Response SERVER_ERROR_RESPONSE = Response.status(Status.INTERNAL_SERVER_ERROR).build();
	public static final Response CONFLICT_RESPONSE = Response.status(Status.CONFLICT).build();

	public static final String BASE_DATA_INFO_NAME = "basedatainfo";
	public static final String LOGIC_NAME = "ENQUETE_RESOURCE";
	public static final String MEMCACHE_CLIENT = "MEMCACHE_CLIENT";
	public static final String DATA_ACCESSOR_NAME = "DATA_ACCESSOR";
	public static final String ACCESS_USERS = "";
}
