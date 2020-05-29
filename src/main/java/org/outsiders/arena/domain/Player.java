package org.outsiders.arena.domain;

import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Table
public class Player
{
  @PrimaryKey
  private int id;
  private String avatarUrl;
  private String displayName;
  private int level;
  
  public int getId()
  {
    return this.id;
  }
  
  public void setId(int id)
  {
    this.id = id;
  }

  public String getAvatarUrl()
  {
    return this.avatarUrl;
  }
  
  public void setAvatarUrl(String avatarUrl)
  {
    this.avatarUrl = avatarUrl;
  }
  
  public int getLevel()
  {
    return this.level;
  }
  
  public void setLevel(int level)
  {
    this.level = level;
  }
  
  public String getDisplayName()
  {
    return this.displayName;
  }
  
  public void setDisplayName(String displayName)
  {
    this.displayName = displayName;
  }

	@Override
	public String toString() {
		return "Player [id=" + id + ", " + (avatarUrl != null ? "avatarUrl=" + avatarUrl + ", " : "")
				+ (displayName != null ? "displayName=" + displayName + ", " : "") + "level=" + level + "]";
	}
}
