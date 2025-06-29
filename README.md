# GroupSystem Plugin

### Minecraft Version: 1.21.4
### Database: PostgreSQL

| Env Variable         | Description                      | Example Value                               |
|----------------------|---------------------------------|---------------------------------------------|
| `DB_CONNECTION_STRING` | JDBC connection string to Postgres DB | `jdbc:postgresql://localhost:7575/postgres` |
| `DB_USERNAME`          | Database username               | `postgres`                                  |
| `DB_PASSWORD`          | Database password               | `password`                                  |


---

## Commands

### /doIHavePermission
Usage:
```
/doIHavePermission <Permission>
```
Checks if you have the specified permission.

---

### /language
Usage:
```
/language <langId> | list
```
Change your language to the given language ID or list all available languages.

---

### /group
Usage:
```
/group create <Name> <Prefix>              - Create a new group with a prefix  
/group info <Player>                       - Show group info for a player  
/group info                                - Show your own group info  
/group delete <Group>                      - Delete a group  
/group addperm <Group> <Permission>        - Add a permission to a group  
/group removeperm <Name> <Permission>      - Remove a permission from a group  
/group set <Player> <Group> <Days> <Hours> <Minutes> <Seconds> - Set a group for a player with expiry  
/group set <Player> <Group>                - Set a group for a player permanently  
/group list                                - List all groups  
```

---

### /addInfoSign
Usage:
```
/addInfoSign
```
Used to add an info sign at the block you are looking at.

---
