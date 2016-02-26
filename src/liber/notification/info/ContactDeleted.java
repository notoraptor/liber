package liber.notification.info;

import liber.data.Contact;
import liber.notification.Info;

/** TODO: Gestion de la suppression à distanced'un contact.
 * Lorsqu'un contact coupe la relation, une option doit être proposée au compte courant
 * pour sauvegarder l'historique de discussion dans un fichier donné.
 * TODO: Gestion privée de la suppression d'un contact.
 * Idéalement, pour des questions de vie privée, un contact qui coupe une relation
 * peut ne pas vouloir que le compte courant sache qu'il a supprimé la relation.
 * Il peut donc être préférable de ne pas supprimer localement le contact, mais de se comporter constamment
 * comme si le contact distant était injoignable, c'est-à-dire:
 * * plus aucun message reçu de sa part.
 * * impossibilité de lui envoyer des messages.
 * Mais dans un tel cas, il faut gérer des situations plus complexes.
 * Par exemple, si le contact distant renvoie une demande de mise en relation pour recréer le lien,
 * cette demande doit être automatiquement acceptée.
 */
public class ContactDeleted extends Info<Contact> {
	public ContactDeleted(Contact contact) {
		super(contact);
	}
}
