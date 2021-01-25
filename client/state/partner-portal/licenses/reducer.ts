/**
 * External dependencies
 */
import { AnyAction } from 'redux';

/**
 * Internal dependencies
 */
import {
	JETPACK_PARTNER_PORTAL_LICENSES_UPDATE,
	JETPACK_PARTNER_PORTAL_LICENSES_REQUEST,
	JETPACK_PARTNER_PORTAL_LICENSES_REQUEST_FAILURE,
	JETPACK_PARTNER_PORTAL_LICENSES_REQUEST_SUCCESS,
} from 'calypso/state/action-types';
import { combineReducers, withoutPersistence } from 'calypso/state/utils';

export const initialState = {
	hasFetched: false,
	isFetching: false,
	all: [],
	error: '',
};

export const hasFetched = withoutPersistence(
	( state = initialState.hasFetched, action: AnyAction ) => {
		switch ( action.type ) {
			case JETPACK_PARTNER_PORTAL_LICENSES_REQUEST_SUCCESS:
			case JETPACK_PARTNER_PORTAL_LICENSES_REQUEST_FAILURE:
				return true;
		}

		return state;
	}
);

export const isFetching = withoutPersistence(
	( state = initialState.isFetching, action: AnyAction ) => {
		switch ( action.type ) {
			case JETPACK_PARTNER_PORTAL_LICENSES_REQUEST:
				return true;

			case JETPACK_PARTNER_PORTAL_LICENSES_REQUEST_SUCCESS:
			case JETPACK_PARTNER_PORTAL_LICENSES_REQUEST_FAILURE:
				return false;
		}

		return state;
	}
);

export const all = withoutPersistence( ( state = initialState.all, action: AnyAction ) => {
	switch ( action.type ) {
		case JETPACK_PARTNER_PORTAL_LICENSES_UPDATE:
			return action.licenses;
	}

	return state;
} );

export const error = withoutPersistence( ( state = initialState.error, action: AnyAction ) => {
	switch ( action.type ) {
		case JETPACK_PARTNER_PORTAL_LICENSES_REQUEST_FAILURE:
			return `${ action.error.status }: ${ action.error.message }`;
	}

	return state;
} );

export default combineReducers( {
	hasFetched,
	isFetching,
	all,
	error,
} );
