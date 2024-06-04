package com.app.config;

public class AppConstant {

    public static final String PAGE_NUMBER = "0";
    public static final String PAGE_SIZE = "10";
    public static final long JWT_TOKEN_VALIDITY = 5 * 60 * 60;

    public static final Long ADMIN_ID = 101L;
    public static final Long USER_ID = 102L;
    public static final String[] PUBLIC_URLS = { "/v3/api-docs/**", "/swagger-ui/**", "/api/registerUser", "/api/verify-account/email", "/api/login" };

    public static final String SORT_EXPENSES_BY = "createdAt";

    public static final String SORT_DIR = "asc";
}
