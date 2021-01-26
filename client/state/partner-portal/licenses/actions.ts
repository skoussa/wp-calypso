/**
 * External dependencies
 */
import { AnyAction } from 'redux';
import debounce from 'lodash/debounce';

/**
 * Internal dependencies
 */
import {
	JETPACK_PARTNER_PORTAL_LICENSES_UPDATE,
	JETPACK_PARTNER_PORTAL_LICENSES_REQUEST,
	JETPACK_PARTNER_PORTAL_LICENSES_REQUEST_FAILURE,
	JETPACK_PARTNER_PORTAL_LICENSES_REQUEST_SUCCESS,
} from 'calypso/state/action-types';
import { ReduxDispatch } from 'calypso/state/redux-store';
import { License } from 'calypso/state/partner-portal';
import { makeCancellable } from 'calypso/utils';
import wpcom from 'calypso/lib/wp';

// Required for modular state.
import 'calypso/state/partner-portal/init';

export function setLicenses( licenses: License[] ): AnyAction {
	return { type: JETPACK_PARTNER_PORTAL_LICENSES_UPDATE, licenses };
}

const formatLicense = ( license ) => ( {
	id: license.id,
	licenseKey: license.license_key,
	issuedAt: license.issued_at,
	attachedAt: license.attached_at,
	revokedAt: license.revoked_at,
	domain: license.domain,
	product: license.product,
	username: license.username,
	blogId: license.blog_id,
} );

let fetchLicensesPromise: ReturnType< typeof makeCancellable > | null = null;

const doFetchLicenses = debounce( ( dispatch: ReduxDispatch ): void => {
	if ( fetchLicensesPromise ) {
		fetchLicensesPromise.cancel();
	}

	fetchLicensesPromise = makeCancellable( wpcom.undocumented().getJetpackPartnerPortalLicenses() );

	fetchLicensesPromise.promise.then(
		( licenses: License[] ) => {
			licenses = licenses.map( formatLicense );

			dispatch( {
				type: JETPACK_PARTNER_PORTAL_LICENSES_REQUEST_SUCCESS,
				licenses,
			} );

			dispatch( setLicenses( licenses ) );
		},
		( error ) => {
			if ( error.isCancelled ) {
				return;
			}

			dispatch( {
				type: JETPACK_PARTNER_PORTAL_LICENSES_REQUEST_FAILURE,
				error: {
					status: error.status,
					code: error.code || '',
					message: error.message,
				},
			} );
		}
	);
}, 300 );

export function fetchLicenses( dispatch: ReduxDispatch, getState: () => unknown ): void {
	dispatch( { type: JETPACK_PARTNER_PORTAL_LICENSES_REQUEST } );

	doFetchLicenses( dispatch );
}
