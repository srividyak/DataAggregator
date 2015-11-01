INSERT INTO SOURCE (source, url, search_url_format, type, updated_at, created_at) values ('flipkart', 'http://flipkart.com', 'http://www.flipkart.com/search?q=SEARCH_QUERY&page=PAGE_NUMBER', 'ECOMMERCE', now(), now());

set @source_id=(select id from source limit 1);
INSERT INTO JOB (url, source_id, page_type, is_regex, is_paginated, is_master, updated_at, created_at) values ('http://www.flipkart.com/', @source_id, 'HTML', 0, 0, 1, now(), now());
INSERT INTO JOB (url, source_id, page_type, is_regex, is_paginated, is_master, updated_at, created_at) values ('http://www.flipkart.com/search?q=FLIPKART_SEARCH_QUERY&start=FLIPKART_NEXT_INDEX', @source_id, 'HTML', 1, 1, 0, now(), now());

set @job_id=(select id from job where is_master=1 limit 1);
INSERT INTO VARIABLE_GROUP (group_id, group_data_type, job_id) values ('group_id', 'com.dataaggregator.core.es.entities.ecommerce.ECommerceSearchData', @job_id);
set @group_var_id=(select id from variable_group limit 1);
INSERT INTO PAGE_VARIABLE (group_variable_id, variable_id, variable_data_type, attribute, job_id, xpath) values (@group_var_id, 'searchQuery', 'java.lang.String', 'data-tracking-id', @job_id, '.goquickly-list li a');
