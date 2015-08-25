/**
 * Copyright 2013 51zhuanfa Inc. All Rights Reserved. 
 */
package com.zhuanfa.service;

import java.util.logging.Logger;

import bigtable.ext.DatastoreTemplate;

/**
 * @author panhz
 * 
 */
public class BaseService {

	protected final Logger log = Logger.getLogger(getClass().getName());

	protected DatastoreTemplate template = new DatastoreTemplate();

}
