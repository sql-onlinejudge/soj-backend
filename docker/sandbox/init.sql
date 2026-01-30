CREATE USER 'sandbox_readonly'@'%' IDENTIFIED BY 'sandbox_readonly';
GRANT SELECT ON sandbox.* TO 'sandbox_readonly'@'%';
FLUSH PRIVILEGES;
