-- Table to store scraped data, including metadata like URL and timestamp
CREATE TABLE scraped_data (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    url VARCHAR(500) NOT NULL,
    content TEXT NOT NULL,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    keywords TEXT  -- Storing keywords associated with this content, comma-separated if needed
);

-- Table to manage scheduled jobs (optional, for tracking job status and schedule times)
CREATE TABLE scraping_job (
    job_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    scheduled_at TIMESTAMP,  -- Time the job is scheduled for
    started_at TIMESTAMP DEFAULT NULL,  -- Time the job actually started
    completed_at TIMESTAMP DEFAULT NULL,  -- Time the job finished
    status ENUM('PENDING', 'IN_PROGRESS', 'COMPLETED', 'FAILED') DEFAULT 'PENDING',
    urls TEXT,  -- URLs to be scraped, stored as comma-separated values
    keywords TEXT  -- Keywords associated with this job, also comma-separated
);

-- Optional indexes for faster searching on commonly queried fields
CREATE INDEX idx_scraped_data_url ON scraped_data (url);
CREATE INDEX idx_scraped_data_timestamp ON scraped_data (timestamp);
CREATE INDEX idx_scraping_job_status ON scraping_job (status);
